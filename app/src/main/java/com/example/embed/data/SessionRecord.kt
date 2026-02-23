package com.example.embed.data

import java.io.Serializable

data class SessionRecord(
    val id: Int = 0,
    var timestamp: Long,
    val score: Int,
    val highestStreak: Int,
    val totalQuestions: Int,
    val correctAnswers: Int
) : Serializable {
    val accuracyPercent: Int
        get() = if (totalQuestions == 0) 0
        else (correctAnswers * 100) / totalQuestions
}