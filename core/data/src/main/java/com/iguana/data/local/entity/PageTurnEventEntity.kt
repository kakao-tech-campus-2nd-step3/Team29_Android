package com.iguana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page_turn_events")
data class PageTurnEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: Long,
    val prevPage: Int,
    val nextPage: Int,
    val timestamp: Double
)