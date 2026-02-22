package com.example.embed.srs

import kotlin.math.roundToInt

object SpacedRepetitionEngine {
    fun onCorrect(card: CardState, now: Long = System.currentTimeMillis()): CardState {
        val dayMillis: Long = 1000 * 60 * 60 * 24
        val returnCard = card.copy()

        returnCard.interval = (returnCard.interval * returnCard.easeFactor).roundToInt()
        returnCard.dueDate = now + returnCard.interval * dayMillis

        return returnCard
    }

    fun onWrong(card: CardState, now: Long = System.currentTimeMillis()): CardState {
        val returnCard = card.copy()

        returnCard.interval = 1
        returnCard.dueDate = now
        returnCard.easeFactor = maxOf(returnCard.easeFactor - 0.2f, 1.3f)

        return returnCard
    }
}