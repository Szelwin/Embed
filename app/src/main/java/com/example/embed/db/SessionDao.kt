package com.example.embed.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(entity: SessionRecordEntity)

    @Query("SELECT * FROM session_records ORDER BY timestamp DESC")
    suspend fun getAllDescending(): List<SessionRecordEntity>
}