package com.example.embed.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.embed.data.SessionRecord

@Entity(tableName = "session_records")
data class SessionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,        // epoch-millis
    val score: Int,
    val highestStreak: Int,
    val totalQuestions: Int,
    val correctAnswers: Int
)

/**
 *   Those functions convert to/from the [SessionRecord] domain model we use in the app.
 *   The [SessionRecordEntity] class is only used for the database part for persistence.
 * */

fun SessionRecordEntity.toDomain() = SessionRecord(
    id = id,
    timestamp = timestamp,
    score = score,
    highestStreak = highestStreak,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers
)

fun SessionRecord.toEntity() = SessionRecordEntity(
    id             = id,
    timestamp      = timestamp,
    score          = score,
    highestStreak  = highestStreak,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers
)