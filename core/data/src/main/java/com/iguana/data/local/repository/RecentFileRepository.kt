package com.iguana.data.local.repository

import com.iguana.data.local.dao.RecentFileDao
import com.iguana.data.local.entity.RecentFileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentFileRepository @Inject constructor(
    private val recentFileDao: RecentFileDao
) {
    suspend fun insertRecentFile(id: String, fileName: String, fileUri: String) {
        withContext(Dispatchers.IO) {
            val recentFile = RecentFileEntity(id, fileName, fileUri, System.currentTimeMillis(), null)
            recentFileDao.insertRecentFile(recentFile)
        }
    }

    fun getRecentFiles(): Flow<List<RecentFileEntity>> {
        return recentFileDao.getRecentFiles()
    }

    suspend fun updateBookmark(fileId: String, bookmarkedPage: Int) {
        val recentFile = recentFileDao.getRecentFileById(fileId)
        recentFile?.let {
            val updatedFile = it.copy(bookmarkedPage = bookmarkedPage)
            recentFileDao.updateRecentFile(updatedFile)
        }
    }

    suspend fun deleteRecentFile(recentFile: RecentFileEntity) {
        recentFileDao.deleteRecentFile(recentFile)
    }

    suspend fun cleanupOldFiles(daysToKeep: Int) {
        val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        recentFileDao.deleteOldFiles(cutoffTime)
    }
}
