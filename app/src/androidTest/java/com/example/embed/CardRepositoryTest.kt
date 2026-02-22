package com.example.embed

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.embed.db.AppDatabase
import com.example.embed.db.CardRepository
import com.example.embed.srs.CardState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: CardRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        repo = CardRepository(db.cardStateDao())
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    @Test
    fun saveAndLoad_roundTrip() = runBlocking {
        val card = CardState(questionId = 7, interval = 3, easeFactor = 2.2f, dueDate = 99999L)
        repo.save(card)
        val loaded = repo.load(7)
        assertEquals(card, loaded)
    }

    @Test
    fun update_overwritesExisting() = runBlocking {
        val card = CardState(questionId = 1, interval = 1, easeFactor = 2.5f, dueDate = 0L)
        repo.save(card)
        val updated = card.copy(interval = 5, dueDate = 86400000L)
        repo.save(updated)
        assertEquals(5, repo.load(1)?.interval)
    }

    @Test
    fun loadNonExistent_returnsNull() = runBlocking {
        assertNull(repo.load(999))
    }
}