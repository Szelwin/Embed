package com.example.embed.data

data class SessionRecord(
    val id: Int = 0,
    var timestamp: Long,
    val score: Int,
    val highestStreak: Int,
    val totalQuestions: Int,
    val correctAnswers: Int
) {
    val accuracyPercent: Int
        get() = if (totalQuestions == 0) 0
        else (correctAnswers * 100) / totalQuestions
}