package com.iguana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iguana.data.local.entity.PageTurnEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageTurnEvent(event: PageTurnEventEntity)

    @Query("SELECT * FROM page_turn_events WHERE documentId = :documentId")
    fun getPageTurnEvents(documentId: Long): Flow<List<PageTurnEventEntity>>

    @Query("DELETE FROM page_turn_events WHERE documentId = :documentId")
    suspend fun deletePageTurnEvents(documentId: Long): Integer
}

