package com.iguana.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iguana.data.local.dao.RecentFileDao
import com.iguana.data.local.entity.RecentFileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RecentFileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao

    private class AppDatabaseCallback(private val database: AppDatabase, private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch(Dispatchers.IO) {
                populateDatabase(database.recentFileDao())
            }
        }

        suspend fun populateDatabase(recentFileDao: RecentFileDao) {
            // 데이터베이스가 생성될 때 초기 데이터를 삽입하거나 다른 설정을 수행할 수 있습니다.
            // 예: recentFileDao.insertRecentFile(RecentFileEntity(...))
        }
    }
}