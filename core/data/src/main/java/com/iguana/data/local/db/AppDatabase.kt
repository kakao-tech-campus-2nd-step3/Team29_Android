package com.iguana.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iguana.data.local.dao.AnnotationDao
import com.iguana.data.local.dao.RecentFileDao
import com.iguana.data.local.dao.PageTurnEventDao
import com.iguana.data.local.entity.AnnotationEntity
import com.iguana.data.local.entity.PageTurnEventEntity
import com.iguana.data.local.entity.RecentFileEntity

@Database(entities = [RecentFileEntity::class, PageTurnEventEntity::class, AnnotationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun pageTurnEventDao(): PageTurnEventDao

    abstract fun annotationDao(): AnnotationDao
}