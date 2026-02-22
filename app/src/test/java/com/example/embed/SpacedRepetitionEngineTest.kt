package com.example.embed

import com.example.embed.srs.CardState
import com.example.embed.srs.SpacedRepetitionEngine
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [SpacedRepetitionEngine].
 */

class SpacedRepetitionEngineTest {
    @Test
    fun `correct answer increases interval`() {
        val card = CardState(questionId = 1, interval = 2, easeFactor = 2.5f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onCorrect(card)

        assertEquals(5, result.interval)  // 2 * 2.5 = 5.0 → 5
    }

    @Test
    fun `wrong answer resets interval`() {
        val card = CardState(questionId = 1, interval = 2, easeFactor = 2.5f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onWrong(card)

        assertEquals(1, result.interval)
    }

    @Test
    fun `ease factor floor at 1,3`() {
        val card = CardState(questionId = 1, interval = 2, easeFactor = 1.3f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onWrong(card)

        assertEquals(1.3f, result.easeFactor)
    }

    @Test
    fun `dueDate OK on correct answer`() {
        val dayMillis: Long = 1000 * 60 * 60 * 24

        val card = CardState(questionId = 1, interval = 1, easeFactor = 2.0f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onCorrect(card, 0)

        assertEquals(dayMillis * 2, result.dueDate)
    }

    @Test
    fun `dueDate set to now on wrong answer`() {
        val card = CardState(questionId = 1, interval = 1, easeFactor = 2.0f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onWrong(card, 12345)

        assertEquals(12345, result.dueDate)
    }

    @Test
    fun `ease factor unchanged on correct answer`() {
        val card = CardState(questionId = 1, interval = 2, easeFactor = 2.0f, dueDate = 0L)
        val result = SpacedRepetitionEngine.onCorrect(card)

        assertEquals(2.0f, result.easeFactor)
    }
}