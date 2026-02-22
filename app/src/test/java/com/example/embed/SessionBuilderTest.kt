package com.example.embed

import com.example.embed.data.Domain
import com.example.embed.data.Question
import com.example.embed.quiz.SessionBuilder
import com.example.embed.srs.CardState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertFalse
import org.junit.Test

class SessionBuilderTest {

    private val now = System.currentTimeMillis()
    private val past = now - 10_000L
    private val future = now + 10_000L

    // 10 questions to work with
    private val allQuestions = (1..10).map { id ->
        Question(
            id = id,
            text = "Question $id",
            options = listOf("A", "B", "C", "D"),
            correctIndex = 0,
            explanation = "E$id",
            domain = Domain.MEMORY,
        )
    }

    // Questions 1–3 are due, 4–5 are seen but not due, 6–10 are new
    private val cardStates = listOf(
        CardState(questionId = 1, dueDate = past),
        CardState(questionId = 2, dueDate = past),
        CardState(questionId = 3, dueDate = past),
        CardState(questionId = 4, dueDate = future),
        CardState(questionId = 5, dueDate = future),
    )

    @Test
    fun `returns exactly sessionLength questions when enough exist`() {
        val result = SessionBuilder(allQuestions, cardStates, 5, now).buildSession()
        assertEquals(5, result.size)
    }

    @Test
    fun `returns all available questions when fewer than sessionLength exist`() {
        val result = SessionBuilder(allQuestions, cardStates, 20, now).buildSession()
        assertEquals(10, result.size)
    }

    @Test
    fun `due questions are included before new ones`() {
        // Ask for exactly 3 — all three should be the due ones
        val result = SessionBuilder(allQuestions, cardStates, 3, now).buildSession()
        val resultIds = result.map { it.id }.toSet()
        assertTrue(resultIds.containsAll(setOf(1, 2, 3)))
    }

    @Test
    fun `new questions are included before old seen-but-not-due ones`() {
        // 3 due + 5 new = 8 questions before we touch old ones
        // Ask for 8 — should not include IDs 4 or 5
        val result = SessionBuilder(allQuestions, cardStates, 8, now).buildSession()
        val resultIds = result.map { it.id }.toSet()
        assertFalse(4 in resultIds)
        assertFalse(5 in resultIds)
    }

    @Test
    fun `old questions are used as last resort backfill`() {
        // Ask for all 10 — old questions 4 and 5 must appear to fill the list
        val result = SessionBuilder(allQuestions, cardStates, 10, now).buildSession()
        val resultIds = result.map { it.id }.toSet()
        assertTrue(4 in resultIds)
        assertTrue(5 in resultIds)
    }

    @Test
    fun `no duplicate questions in result`() {
        val result = SessionBuilder(allQuestions, cardStates, 10, now).buildSession()
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }

    @Test
    fun `returns empty list when no questions exist`() {
        val result = SessionBuilder(emptyList(), emptyList(), 5, now).buildSession()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `works correctly with no card states at all`() {
        // Everything is new — should just return sessionLength new questions
        val result = SessionBuilder(allQuestions, emptyList(), 4, now).buildSession()
        assertEquals(4, result.size)
    }
}