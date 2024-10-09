package com.iguana.domain.repository

import android.net.Uri
import com.iguana.domain.model.RecentFile
import kotlinx.coroutines.flow.Flow

interface RecentFileRepository {
    suspend fun insertRecentFile(id: Long, fileName: String, fileUri: String)
    fun getRecentFiles(): Flow<List<RecentFile>>
    suspend fun updateBookmark(fileId: Long, bookmarkedPage: Int)
    suspend fun deleteRecentFile(recentFile: RecentFile)
    suspend fun cleanupOldFiles(daysToKeep: Int)
}
