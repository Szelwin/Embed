package com.example.embed

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.embed.data.Domain
import com.example.embed.settings.SettingsManager
import com.example.embed.ui.theme.EmbedTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsActivityTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var settings: SettingsManager
    private var saveCalled = false
    private var backCalled = false

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Clear prefs so each test starts fresh
        context.getSharedPreferences("embed_settings", Context.MODE_PRIVATE)
            .edit().clear().commit()

        settings = SettingsManager(context)
        saveCalled = false
        backCalled = false
    }

    private fun launchScreen() {
        composeRule.setContent {
            EmbedTheme {
                SettingsScreen(
                    settingsManager = settings,
                    onSave = { saveCalled = true },
                    onGoBack = { backCalled = true }
                )
            }
        }
    }

    @Test
    fun defaultState() {
        launchScreen()

        composeRule.onNodeWithText("Questions per session:").assertIsDisplayed()
        composeRule.onNodeWithText("10").assertIsDisplayed()

        for (domain in Domain.entries) {
            composeRule.onNodeWithText(domain.displayName).assertIsDisplayed()
        }
    }

    @Test
    fun incrementQuestionCount() {
        launchScreen()

        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("11").assertIsDisplayed()
    }

    @Test
    fun decrementQuestionCount() {
        launchScreen()

        composeRule.onNodeWithText("-").performClick()
        composeRule.onNodeWithText("9").assertIsDisplayed()
    }
}