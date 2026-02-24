package com.example.embed.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.embed.data.Domain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsManagerTest {

    private lateinit var settings: SettingsManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        settings = SettingsManager(context)
    }

    @Test
    fun defaultQuestionCount() {
        assertEquals(10, settings.questionsPerSession)
    }

    @Test
    fun setAndReadQuestionCount20() {
        settings.questionsPerSession = 20
        assertEquals(20, settings.questionsPerSession)
    }

    @Test
    fun setAndReadQuestionCount0() {
        // Expect 1, since we don't allow less than 1 and clamp
        settings.questionsPerSession = 0
        assertEquals(1, settings.questionsPerSession)
    }

    @Test
    fun setAndReadQuestionCount100() {
        // Expect 40, since we don't allow more than 40 and clamp
        settings.questionsPerSession = 100
        assertEquals(40, settings.questionsPerSession)
    }

    @Test
    fun defaultQuestionDomains() {
        assertEquals(Domain.entries.toSet(), settings.enabledDomains)
    }

    @Test
    fun setAndReadQuestionDomainsSingle() {
        val domains = setOf(Domain.MEMORY)
        settings.enabledDomains = domains
        assertEquals(domains, settings.enabledDomains)
    }

    @Test
    fun setAndReadQuestionDomainsMultiple() {
        val domains = setOf(Domain.MEMORY, Domain.FIRMWARE, Domain.PROTOCOLS)
        settings.enabledDomains = domains
        assertEquals(domains, settings.enabledDomains)
    }

    @Test
    fun setAndReadQuestionDomainsEmpty() {
        // Expect the default full set, since we don't allow empty
        val domains = emptySet<Domain>()
        settings.enabledDomains = domains
        assertEquals(Domain.entries.toSet(), settings.enabledDomains)
    }
}