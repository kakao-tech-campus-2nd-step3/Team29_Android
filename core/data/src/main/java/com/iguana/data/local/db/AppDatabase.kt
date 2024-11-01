package com.iguana.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iguana.data.local.dao.RecentFileDao
import com.iguana.data.local.entity.PageTurnEventDao
import com.iguana.data.local.entity.PageTurnEventEntity
import com.iguana.data.local.entity.RecentFileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RecentFileEntity::class, PageTurnEventEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun pageTurnEventDao(): PageTurnEventDao
}