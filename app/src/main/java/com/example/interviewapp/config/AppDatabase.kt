package com.example.interviewapp.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.interviewapp.dao.SportRecordDao
import com.example.interviewapp.entity.SportRecord

@Database(entities = [SportRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): SportRecordDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "records.db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}
