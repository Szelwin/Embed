package com.example.embed.settings

import android.content.Context
import android.content.SharedPreferences
import com.example.embed.data.Domain
import androidx.core.content.edit

class SettingsManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "embed_settings"
        private const val KEY_QUESTIONS_PER_SESSION = "questions_per_session"
        private const val KEY_ENABLED_DOMAINS = "enabled_domains"
        const val DEFAULT_QUESTIONS_PER_SESSION = 10
        const val MIN_QUESTIONS = 1
        const val MAX_QUESTIONS = 40
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var questionsPerSession: Int
        get() = prefs.getInt(KEY_QUESTIONS_PER_SESSION, DEFAULT_QUESTIONS_PER_SESSION)
        set(value) {
            val clamped = value.coerceIn(MIN_QUESTIONS, MAX_QUESTIONS)
            prefs.edit { putInt(KEY_QUESTIONS_PER_SESSION, clamped) }
        }

    var enabledDomains: Set<Domain>
        get() {
            val defaultDomainSet = Domain.entries.toSet()
            val defaultDomainSetNames = defaultDomainSet.map { it.name }.toSet()

            val savedSet = prefs.getStringSet(KEY_ENABLED_DOMAINS, defaultDomainSetNames)
                ?: defaultDomainSetNames

            return savedSet.mapNotNull { domainName ->
                Domain.entries.find { it.name == domainName }
            }.toSet()
        }
        set(domainSet) {
            if (domainSet.isEmpty()) return
            val domainSetNames = domainSet.map { it.name }.toSet()
            prefs.edit { putStringSet(KEY_ENABLED_DOMAINS, domainSetNames) }
        }
}