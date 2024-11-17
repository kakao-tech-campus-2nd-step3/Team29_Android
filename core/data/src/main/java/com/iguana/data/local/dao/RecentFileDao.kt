package com.iguana.data.local.dao

import androidx.room.*
import com.iguana.data.local.entity.RecentFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentFile(recentFile: RecentFileEntity)

    @Query("SELECT * FROM recent_files WHERE id = :id")
    fun getRecentFileById(id: Long): RecentFileEntity?

    @Query("SELECT * FROM recent_files ORDER BY lastOpened DESC")
    fun getRecentFiles(): Flow<List<RecentFileEntity>>

    @Update
    fun updateRecentFile(recentFile: RecentFileEntity)

    @Query("UPDATE recent_files SET lastOpened = :lastOpened WHERE id = :id")
    fun updateLastOpened(id: String, lastOpened: String): Int

    @Query("DELETE FROM recent_files WHERE id = :id")
    fun deleteRecentFile(id: Long)

    @Query("DELETE FROM recent_files WHERE lastOpened < :thresholdDate")
    fun deleteOldFiles(thresholdDate: String): Int

    @Query("UPDATE recent_files SET fileName = :fileName WHERE id = :fileId")
    fun updateFileName(fileId: Long, fileName: String)
}
