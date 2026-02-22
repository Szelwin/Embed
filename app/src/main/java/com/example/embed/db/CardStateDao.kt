package com.example.embed.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CardStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CardStateEntity)

    @Query("SELECT * FROM card_states WHERE questionId = :id")
    suspend fun getById(id: Int): CardStateEntity?

    @Query("SELECT * FROM card_states")
    suspend fun getAll(): List<CardStateEntity>
}