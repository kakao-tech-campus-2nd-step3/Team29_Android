package com.iguana.domain.repository

import com.iguana.data.local.entity.RecentFileEntity
import kotlinx.coroutines.flow.Flow

interface RecentFileRepository {
    suspend fun insertRecentFile(id: String, fileName: String, fileUri: String)
    fun getRecentFiles(): Flow<List<RecentFileEntity>>
    suspend fun updateBookmark(fileId: String, bookmarkedPage: Int)
    suspend fun deleteRecentFile(recentFile: RecentFileEntity)
    suspend fun cleanupOldFiles(daysToKeep: Int)
}
