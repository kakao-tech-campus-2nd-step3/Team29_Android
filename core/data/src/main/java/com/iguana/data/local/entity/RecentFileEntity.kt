package com.iguana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val fileUri: String,
    val lastOpened: Long,
    val bookmarkedPage: Int?
)