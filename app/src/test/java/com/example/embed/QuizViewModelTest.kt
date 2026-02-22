package com.example.embed

import com.example.embed.data.Domain
import com.example.embed.data.Question
import com.example.embed.quiz.QuizViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [QuizViewModel].
 */

class QuizViewModelTest {
    fun createTestQuestion(id: Int, correctIndex: Int): Question {
        return Question(
            id = id,
            text = "Q$id",
            options = listOf("A$id", "B$id", "C$id", "D$id"),
            correctIndex = correctIndex,
            explanation = "E$id",
            domain = Domain.MEMORY,
            hasCodeBlock = false
        )
    }

    fun getTestQuestions(): List<Question> {
        val q1 = createTestQuestion(0, 3)
        val q2 = createTestQuestion(1, 2)
        val q3 = createTestQuestion(2, 1)
        val q4 = createTestQuestion(3, 0)

        return listOf(q1, q2, q3, q4)
    }

    @Test
    fun `correct answer increases score by 10`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)

        assertEquals(10, viewModel.score)
    }

    @Test
    fun `wrong answer decreases score by 5`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(0)

        assertEquals(5, viewModel.score)
    }

    @Test
    fun `score never gets below 0`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(0)

        assertEquals(0, viewModel.score)
    }

    @Test
    fun `streak increments`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)

        assertEquals(2, viewModel.streak)
    }

    @Test
    fun `streak resets`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)
        viewModel.submitAnswer(3)

        assertEquals(0, viewModel.streak)
    }

    @Test
    fun `streak keeps highest streak`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)
        viewModel.submitAnswer(3)

        assertEquals(2, viewModel.highestStreak)
    }

    @Test
    fun `finished is false on start`() {
        val viewModel = QuizViewModel(getTestQuestions())
        assertEquals(false, viewModel.isFinished)
    }

    @Test
    fun `finished is false mid-quiz`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)

        assertEquals(false, viewModel.isFinished)
    }

    @Test
    fun `finished is true when done`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)

        assertEquals(true, viewModel.isFinished)
    }

    @Test
    fun `correct answer count increments properly`() {
        val viewModel = QuizViewModel(getTestQuestions())
        viewModel.submitAnswer(3)
        viewModel.submitAnswer(2)
        viewModel.submitAnswer(0)
        viewModel.submitAnswer(0)

        assertEquals(3, viewModel.correctAnswers)
    }
}