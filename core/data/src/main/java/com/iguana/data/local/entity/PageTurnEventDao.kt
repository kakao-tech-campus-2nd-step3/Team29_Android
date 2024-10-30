package com.iguana.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PageTurnEventDao {

    @Insert
    fun insert(event: PageTurnEventEntity)

    @Query("SELECT * FROM page_turn_events WHERE documentId = :documentId")
    fun getEventsByDocumentId(documentId: Long): List<PageTurnEventEntity>

    @Query("DELETE FROM page_turn_events WHERE documentId = :documentId")
    fun deleteEventsByDocumentId(documentId: Long) : Int
}
