package com.example.embed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.embed.ui.theme.EmbedTheme
import org.junit.Rule
import org.junit.Test

class ResultScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun launchResult(score: Int = 55, accuracy: Int = 90, streak: Int = 8) {
        composeRule.setContent {
            EmbedTheme {
                ResultScreen(
                    score = score,
                    accuracy = accuracy,
                    highestStreak = streak
                )
            }
        }
    }

    @Test
    fun titleIsDisplayed() {
        launchResult()
        composeRule.onNodeWithText("Results").assertIsDisplayed()
    }

    @Test
    fun scoreIsDisplayed() {
        launchResult(score = 55)
        composeRule.onNodeWithText("55 points").assertIsDisplayed()
    }

    @Test
    fun accuracyIsDisplayed() {
        launchResult(accuracy = 90)
        composeRule.onNodeWithText("90 %").assertIsDisplayed()
    }

    @Test
    fun highestStreakIsDisplayed() {
        launchResult(streak = 8)
        composeRule.onNodeWithText("8 questions").assertIsDisplayed()
    }

    @Test
    fun bothButtonsAreDisplayed() {
        launchResult()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Home").assertIsDisplayed()
    }
}