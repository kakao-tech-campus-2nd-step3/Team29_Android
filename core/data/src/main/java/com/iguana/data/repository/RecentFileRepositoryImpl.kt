package com.iguana.data.repository

import android.net.Uri
import com.iguana.data.local.dao.RecentFileDao
import com.iguana.data.local.entity.RecentFileEntity
import com.iguana.data.mapper.toDomainModel
import com.iguana.data.mapper.toEntity
import com.iguana.domain.model.RecentFile
import com.iguana.domain.repository.RecentFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentFileRepositoryImpl @Inject constructor(
    private val recentFileDao: RecentFileDao
) : RecentFileRepository {
    override suspend fun insertRecentFile(id: Long, fileName: String, fileUri: String) {
        withContext(Dispatchers.IO) {
            val recentFileEntity =
                RecentFileEntity(id, fileName, fileUri, System.currentTimeMillis(), null)
            recentFileDao.insertRecentFile(recentFileEntity)
        }
    }

    override fun getRecentFiles(): Flow<List<RecentFile>> {
        return recentFileDao.getRecentFiles().map {
            it.map { it.toDomainModel() }
        }
    }

    override suspend fun updateBookmark(fileId: Long, bookmarkedPage: Int) {
        val recentFile = recentFileDao.getRecentFileById(fileId)
        recentFile?.let {
            val updatedFile = it.copy(bookmarkedPage = bookmarkedPage)
            recentFileDao.updateRecentFile(updatedFile)
        }
    }

    override suspend fun deleteRecentFile(recentFile: RecentFile) {
        recentFileDao.deleteRecentFile(recentFile.toEntity())
    }

    override suspend fun cleanupOldFiles(daysToKeep: Int) {
        recentFileDao.deleteOldFiles(daysToKeep)
    }
}
