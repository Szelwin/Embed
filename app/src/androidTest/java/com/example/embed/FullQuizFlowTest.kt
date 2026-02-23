package com.example.embed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.embed.data.Question
import com.example.embed.data.QuestionBank
import com.example.embed.db.AppDatabase
import com.example.embed.db.SessionRecordEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FullQuizFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = AppDatabase.getInstance(composeRule.activity.applicationContext)
        runBlocking { db.clearAllTables() }
    }

    // ── Helpers ──

    private fun findCurrentQuestion(): Question {
        for (q in QuestionBank.all) {
            val nodes = composeRule
                .onAllNodesWithText(q.text, substring = false)
                .fetchSemanticsNodes()
            if (nodes.isNotEmpty()) return q
        }
        throw IllegalStateException("No matching question found on screen")
    }

    private fun answerCorrectly() {
        val q = findCurrentQuestion()
        composeRule.onNodeWithText(q.options[q.correctIndex]).performClick()
    }

    private fun answerIncorrectly() {
        val q = findCurrentQuestion()
        val wrongIndex = (q.correctIndex + 1) % 4
        composeRule.onNodeWithText(q.options[wrongIndex]).performClick()
    }

    /**
     * Plays a full 10-question session.
     * Answers the first [correctCount] correctly, then the rest incorrectly.
     */
    private fun playSession(correctCount: Int) {
        repeat(10) { i ->
            composeRule.waitForIdle()
            if (i < correctCount) answerCorrectly() else answerIncorrectly()
            composeRule.waitForIdle()
            composeRule.onNodeWithText("Next").performClick()
        }
    }

    private fun assertResultsScreen(score: Int, accuracy: Int, streak: Int) {
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithText("Results")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("$score points").assertIsDisplayed()
        composeRule.onNodeWithText("$accuracy %").assertIsDisplayed()
        composeRule.onNodeWithText("$streak questions").assertIsDisplayed()
    }

    private fun getSessionRecords(): List<SessionRecordEntity> {
        Thread.sleep(500)
        return runBlocking { db.sessionDao().getAllDescending() }
    }

    private fun assertSessionCount(expected: Int) {
        assertEquals(expected, getSessionRecords().size)
    }

    private fun assertSessionRecord(
        record: SessionRecordEntity,
        score: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        highestStreak: Int
    ) {
        assertEquals(score, record.score)
        assertEquals(correctAnswers, record.correctAnswers)
        assertEquals(totalQuestions, record.totalQuestions)
        assertEquals(highestStreak, record.highestStreak)
    }

    private fun startQuiz() {
        composeRule.onNodeWithText("Start").performClick()
    }

    private fun goHome() {
        composeRule.onNodeWithText("Home").performClick()
        composeRule.waitUntil(5000) {
            composeRule.onAllNodesWithText("Embed")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Calculates expected score when answering [correct] questions
     * correctly first, then the rest incorrectly.
     * +10 per correct, -5 per wrong, floor at 0.
     */
    private fun expectedScore(correct: Int, total: Int = 10): Int {
        var s = correct * 10
        for (i in 0 until (total - correct)) {
            s = maxOf(0, s - 5)
        }
        return s
    }

    // ── Tests: all correct ──

    @Test
    fun allCorrect_resultsAndDbCorrect() {
        startQuiz()
        playSession(10)
        assertResultsScreen(score = 100, accuracy = 100, streak = 10)

        val records = getSessionRecords()
        assertSessionCount(1)
        assertSessionRecord(records[0], score = 100, correctAnswers = 10,
            totalQuestions = 10, highestStreak = 10)
    }

    @Test
    fun allCorrect_retry_bothSessionsSaved() {
        startQuiz()
        playSession(10)
        assertResultsScreen(score = 100, accuracy = 100, streak = 10)
        assertSessionCount(1)

        composeRule.onNodeWithText("Retry").performClick()
        playSession(10)
        assertResultsScreen(score = 100, accuracy = 100, streak = 10)

        val records = getSessionRecords()
        assertSessionCount(2)
        assertSessionRecord(records[0], score = 100, correctAnswers = 10,
            totalQuestions = 10, highestStreak = 10)
        assertSessionRecord(records[1], score = 100, correctAnswers = 10,
            totalQuestions = 10, highestStreak = 10)
    }

    @Test
    fun allCorrect_goHome_restartQuiz_bothSessionsSaved() {
        startQuiz()
        playSession(10)
        assertResultsScreen(score = 100, accuracy = 100, streak = 10)
        assertSessionCount(1)

        goHome()
        startQuiz()
        playSession(10)
        assertResultsScreen(score = 100, accuracy = 100, streak = 10)

        val records = getSessionRecords()
        assertSessionCount(2)
        assertSessionRecord(records[0], score = 100, correctAnswers = 10,
            totalQuestions = 10, highestStreak = 10)
    }

    // ── Tests: 70% accuracy ──
    // 7 correct first (+70), then 3 wrong (-5 each) → score 55

    @Test
    fun accuracy70_resultsAndDbCorrect() {
        startQuiz()
        playSession(7)
        assertResultsScreen(score = 55, accuracy = 70, streak = 7)

        val records = getSessionRecords()
        assertSessionCount(1)
        assertSessionRecord(records[0], score = 55, correctAnswers = 7,
            totalQuestions = 10, highestStreak = 7)
    }

    @Test
    fun accuracy70_retry_bothSessionsSaved() {
        startQuiz()
        playSession(7)
        assertResultsScreen(score = 55, accuracy = 70, streak = 7)
        assertSessionCount(1)

        composeRule.onNodeWithText("Retry").performClick()
        playSession(7)
        assertResultsScreen(score = 55, accuracy = 70, streak = 7)

        val records = getSessionRecords()
        assertSessionCount(2)
        assertSessionRecord(records[0], score = 55, correctAnswers = 7,
            totalQuestions = 10, highestStreak = 7)
        assertSessionRecord(records[1], score = 55, correctAnswers = 7,
            totalQuestions = 10, highestStreak = 7)
    }

    // ── Tests: 20% accuracy ──
    // 2 correct first (+20), then 8 wrong: 20→15→10→5→0→0→0→0→0 → score 0

    @Test
    fun accuracy20_resultsAndDbCorrect() {
        startQuiz()
        playSession(2)
        assertResultsScreen(score = 0, accuracy = 20, streak = 2)

        val records = getSessionRecords()
        assertSessionCount(1)
        assertSessionRecord(records[0], score = 0, correctAnswers = 2,
            totalQuestions = 10, highestStreak = 2)
    }

    @Test
    fun accuracy20_goHome_restartQuiz_bothSessionsSaved() {
        startQuiz()
        playSession(2)
        assertResultsScreen(score = 0, accuracy = 20, streak = 2)
        assertSessionCount(1)

        goHome()
        startQuiz()
        playSession(2)
        assertResultsScreen(score = 0, accuracy = 20, streak = 2)

        val records = getSessionRecords()
        assertSessionCount(2)
        assertSessionRecord(records[0], score = 0, correctAnswers = 2,
            totalQuestions = 10, highestStreak = 2)
        assertSessionRecord(records[1], score = 0, correctAnswers = 2,
            totalQuestions = 10, highestStreak = 2)
    }

    // ── Tests: 0% accuracy ──
    // All wrong, floor at 0 → score 0, streak 0

    @Test
    fun accuracy0_resultsAndDbCorrect() {
        startQuiz()
        playSession(0)
        assertResultsScreen(score = 0, accuracy = 0, streak = 0)

        val records = getSessionRecords()
        assertSessionCount(1)
        assertSessionRecord(records[0], score = 0, correctAnswers = 0,
            totalQuestions = 10, highestStreak = 0)
    }

    @Test
    fun accuracy0_retry_bothSessionsSaved() {
        startQuiz()
        playSession(0)
        assertResultsScreen(score = 0, accuracy = 0, streak = 0)
        assertSessionCount(1)

        composeRule.onNodeWithText("Retry").performClick()
        playSession(0)
        assertResultsScreen(score = 0, accuracy = 0, streak = 0)

        val records = getSessionRecords()
        assertSessionCount(2)
        assertSessionRecord(records[0], score = 0, correctAnswers = 0,
            totalQuestions = 10, highestStreak = 0)
        assertSessionRecord(records[1], score = 0, correctAnswers = 0,
            totalQuestions = 10, highestStreak = 0)
    }
}