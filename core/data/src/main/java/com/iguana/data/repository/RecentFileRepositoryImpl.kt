package com.iguana.data.repository

import android.net.Uri
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class RecentFileRepositoryImpl @Inject constructor(
    private val recentFileDao: RecentFileDao
) : RecentFileRepository {
    // 현재 날짜를 yyyy-MM-dd HH:mm:ss 형식으로 반환
    private fun getCurrentFormattedDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(System.currentTimeMillis())
    }

    // 주어진 날짜 기준으로 특정 일 수 이전의 날짜 반환
    private fun getPastFormattedDate(daysAgo: Int): String {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(calendar.time)
    }


    override suspend fun insertRecentFile(id: Long, fileName: String, fileUri: String) {
        withContext(Dispatchers.IO) {
            val recentFileEntity =
                RecentFileEntity(id, fileName, fileUri, getCurrentFormattedDate(), null)
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

    override fun updateFileName(fileId: Long, fileName: String) {
        recentFileDao.updateFileName(fileId, fileName)
    }

    override suspend fun deleteRecentFile(id: Long) {
        Log.d("RecentFileRepositoryImpl", "deleteRecentFile: $id")
        recentFileDao.deleteRecentFile(id)
    }

    override suspend fun cleanupOldFiles(daysToKeep: Int) {
        val thresholdDate = getPastFormattedDate(daysToKeep)
        recentFileDao.deleteOldFiles(thresholdDate)
    }
}
