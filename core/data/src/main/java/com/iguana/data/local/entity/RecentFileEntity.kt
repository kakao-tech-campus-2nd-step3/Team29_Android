package com.iguana.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "fileName") val fileName: String,
    @ColumnInfo(name = "fileUri") val fileUri: String,
    @ColumnInfo(name = "lastOpened") val lastOpened: Long,
    @ColumnInfo(name = "bookmarkedPage") val bookmarkedPage: Int?
)