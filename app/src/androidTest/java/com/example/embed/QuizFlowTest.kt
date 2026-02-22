package com.example.embed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.embed.data.Domain
import com.example.embed.data.Question
import com.example.embed.quiz.QuizViewModel
import com.example.embed.ui.theme.EmbedTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class QuizFlowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val allQuestions = (1..10).map { id ->
        Question(
            id = id,
            text = "Stub question $id",
            options = listOf("A", "B", "C", "D"),
            correctIndex = 0,
            explanation = "E$id",
            domain = Domain.MEMORY,
        )
    }

    private fun launchQuiz(
        questions: List<Question> = allQuestions,
        onFinished: () -> Unit = {}
    ): QuizViewModel {
        val vm = QuizViewModel(questions)
        composeRule.setContent {
            EmbedTheme {
                QuizScreen(vm = vm, onSessionFinished = onFinished)
            }
        }
        return vm
    }

    // ── All options displayed ────────────────────────────

    @Test
    fun allFourOptions_displayedOnStart() {
        launchQuiz()

        composeRule.onNodeWithText("A").assertIsDisplayed()
        composeRule.onNodeWithText("B").assertIsDisplayed()
        composeRule.onNodeWithText("C").assertIsDisplayed()
        composeRule.onNodeWithText("D").assertIsDisplayed()
    }

    // ── Answer feedback ─────────────────────────────────

    @Test
    fun correctAnswer_showsNextButton() {
        launchQuiz()

        composeRule.onNodeWithText("Stub question 1").assertIsDisplayed()
        composeRule.onNodeWithText("A").performClick()
        composeRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun wrongAnswer_showsExplanation() {
        launchQuiz()

        composeRule.onNodeWithText("B").performClick()
        composeRule.onNodeWithText("E1").assertIsDisplayed()
        composeRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun correctAnswer_noExplanationShown() {
        launchQuiz()

        composeRule.onNodeWithText("A").performClick()
        composeRule.onNodeWithText("E1").assertDoesNotExist()
    }

    @Test
    fun tappingAnyAnswer_showsNextButton() {
        launchQuiz()

        composeRule.onNodeWithText("C").performClick()
        composeRule.onNodeWithText("Next").assertIsDisplayed()
    }

    // ── Next button visibility ──────────────────────────

    @Test
    fun nextButton_notVisibleBeforeAnswering() {
        launchQuiz()

        composeRule.onNodeWithText("Next").assertDoesNotExist()
    }

    // ── Answer locking ──────────────────────────────────

    @Test
    fun answerCards_disabledAfterSelection() {
        val vm = launchQuiz()

        // Tap wrong answer "B" (index 1)
        composeRule.onNodeWithText("B").performClick()
        // Try tapping correct answer "A" — should have no effect
        composeRule.onNodeWithText("A").performClick()

        // VM should still have recorded index 1, not 0
        assertTrue(vm.selectedAnswerIndex == 1)
    }

    // ── Navigation ──────────────────────────────────────

    @Test
    fun questionCounter_incrementsOnAdvance() {
        launchQuiz()

        for (i in 1..3) {
            composeRule.onNodeWithText("Question $i").assertIsDisplayed()
            composeRule.onNodeWithText("A").performClick()
            composeRule.onNodeWithText("Next").performClick()
        }
    }

    @Test
    fun tappingNext_advancesToQuestion2() {
        launchQuiz()

        composeRule.onNodeWithText("A").performClick()
        composeRule.onNodeWithText("Next").performClick()

        composeRule.onNodeWithText("Question 2").assertIsDisplayed()
        composeRule.onNodeWithText("Stub question 2").assertIsDisplayed()
    }

    @Test
    fun singleQuestionSession_finishesAfterOne() {
        var finished = false
        val single = listOf(allQuestions.first())
        launchQuiz(questions = single, onFinished = { finished = true })

        composeRule.onNodeWithText("A").performClick()
        composeRule.onNodeWithText("Next").performClick()

        assertTrue(finished)
    }

    @Test
    fun fullFlow_allQuestions_sessionFinishes() {
        var finished = false
        launchQuiz(onFinished = { finished = true })

        for (i in 1..10) {
            composeRule.onNodeWithText("Stub question $i").assertIsDisplayed()
            composeRule.onNodeWithText("A").performClick()
            composeRule.onNodeWithText("Next").performClick()
        }

        assertTrue(finished)
    }

    @Test
    fun completingAllQuestions_doesNotCrash() {
        launchQuiz()

        for (i in 1..10) {
            composeRule.onNodeWithText("A").performClick()
            composeRule.onNodeWithText("Next").performClick()
        }
        // No assertion — test passes if no exception is thrown
    }
}