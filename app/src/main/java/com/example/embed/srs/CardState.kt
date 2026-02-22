package com.example.embed.srs

data class CardState(
    val questionId: Int,
    var interval: Int = 1,        // days until next review
    var easeFactor: Float = 2.5f, // how easy this card feels (min 1.3)
    var dueDate: Long = 0L        // epoch-millis of next scheduled review
)