package com.example.embed

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.embed.db.AppDatabase
import com.example.embed.db.SessionRepository
import com.example.embed.data.SessionRecord
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SessionRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: SessionRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        repo = SessionRepository(db.sessionDao())
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    fun getSessionRandom(id: Int = 0): SessionRecord {
        return SessionRecord(
            id = id,
            timestamp = Random.nextLong(1, 1000),
            score = Random.nextInt(1, 50),
            highestStreak = Random.nextInt(1, 50),
            totalQuestions = Random.nextInt(10, 30),
            correctAnswers = Random.nextInt(10, 30)
        )
    }

    @Test
    fun saveAndLoad_single() = runBlocking {
        val record = getSessionRandom(1)

        repo.save(record)

        val loaded = repo.getAll()
        assertEquals(1, loaded.size)
        assertEquals(record, loaded[0])
    }

    @Test
    fun saveAndLoad_multiple() = runBlocking {
        var r1 = getSessionRandom(1)
        var r2 = getSessionRandom(2)
        var r3 = getSessionRandom(3)

        r1.timestamp = 10
        r2.timestamp = 20
        r3.timestamp = 30

        repo.save(r1)
        repo.save(r2)
        repo.save(r3)

        val loaded = repo.getAll()

        assertEquals(3, loaded.size)
        assertEquals(r3, loaded[0])
        assertEquals(r2, loaded[1])
        assertEquals(r1, loaded[2])
    }

    @Test
    fun loadNonExistent_returnsEmptyList() = runBlocking {
        val loaded = repo.getAll()
        assertEquals(0, loaded.size)
    }
}