package com.iguana.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iguana.data.local.dao.RecordingDao
import com.iguana.data.local.entity.PageTurnEventEntity

@Database(entities = [PageTurnEventEntity::class], version = 1, exportSchema = false)
abstract class RecordingDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}