package com.example.embed

import com.example.embed.data.Domain
import com.example.embed.data.QuestionBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [QuestionBank].
 *
 * These tests verify the structural integrity of the question data — they act
 * as a compile-time guard against accidentally broken or malformed questions.
 * Because [QuestionBank] is pure in-memory Kotlin, no Android framework or
 * emulator is needed; these run on the local JVM.
 */
class QuestionBankTest {

    @Test
    fun `bank contains at least 40 questions`() {
        assertTrue(
            "Expected ≥ 40 questions, found ${QuestionBank.all.size}",
            QuestionBank.all.size >= 40
        )
    }

    @Test
    fun `every question has exactly 4 options`() {
        QuestionBank.all.forEach { q ->
            assertEquals(
                "Question ${q.id} has ${q.options.size} options instead of 4",
                4, q.options.size
            )
        }
    }

    @Test
    fun `every correctIndex is in range 0 to 3`() {
        QuestionBank.all.forEach { q ->
            assertTrue(
                "Question ${q.id} has correctIndex ${q.correctIndex} outside 0–3",
                q.correctIndex in 0..3
            )
        }
    }

    @Test
    fun `no question has blank text`() {
        QuestionBank.all.forEach { q ->
            assertTrue(
                "Question ${q.id} has blank text",
                q.text.isNotBlank()
            )
        }
    }

    @Test
    fun `no option text is blank`() {
        QuestionBank.all.forEach { q ->
            q.options.forEachIndexed { index, option ->
                assertTrue(
                    "Question ${q.id}, option $index is blank",
                    option.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `no explanation is blank`() {
        QuestionBank.all.forEach { q ->
            assertTrue(
                "Question ${q.id} has a blank explanation",
                q.explanation.isNotBlank()
            )
        }
    }

    @Test
    fun `all question IDs are unique`() {
        val ids = QuestionBank.all.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals(
            "Duplicate IDs found in QuestionBank",
            ids.size, uniqueIds.size
        )
    }

    @Test
    fun `all six domains are represented`() {
        val coveredDomains = QuestionBank.all.map { it.domain }.toSet()
        Domain.entries.forEach { domain ->
            assertTrue(
                "Domain $domain has no questions in the bank",
                domain in coveredDomains
            )
        }
    }

    @Test
    fun `byId returns correct question`() {
        val q = QuestionBank.byId(1)
        assertNotNull("byId(1) returned null", q)
        assertEquals(1, q!!.id)
    }

    @Test
    fun `byId returns null for unknown id`() {
        val q = QuestionBank.byId(9999)
        assertTrue("byId(9999) should return null", q == null)
    }

    @Test
    fun `forDomains filters correctly`() {
        val memoryOnly = QuestionBank.forDomains(setOf(Domain.MEMORY))
        assertTrue(memoryOnly.isNotEmpty())
        memoryOnly.forEach { q ->
            assertEquals(Domain.MEMORY, q.domain)
        }
    }

    @Test
    fun `no option exceeds 28 characters`() {
        QuestionBank.all.forEach { q ->
            q.options.forEachIndexed { index, option ->
                assertTrue(
                    "Question ${q.id}, option $index is ${option.length} chars (max 28): \"$option\"",
                    option.length <= 28
                )
            }
        }
    }
}