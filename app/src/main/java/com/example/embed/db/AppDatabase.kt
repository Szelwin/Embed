package com.example.embed.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CardStateEntity::class, SessionRecordEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardStateDao(): CardStateDao
    abstract fun sessionDao(): SessionDao
}