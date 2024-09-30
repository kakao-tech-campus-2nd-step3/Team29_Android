package com.iguana.domain.repository

import com.iguana.domain.model.RecentFile
import kotlinx.coroutines.flow.Flow

interface RecentFileRepository {
    suspend fun insertRecentFile(id: String, fileName: String, fileUri: String)
    fun getRecentFiles(): Flow<List<RecentFile>>
    suspend fun updateBookmark(fileId: String, bookmarkedPage: Int)
    suspend fun deleteRecentFile(recentFile: RecentFile)
    suspend fun cleanupOldFiles(daysToKeep: Int)
}
