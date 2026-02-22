package com.example.embed.db

import com.example.embed.data.SessionRecord

class SessionRepository(private val dao: SessionDao) {
    suspend fun save(record: SessionRecord) =
        dao.insert(record.toEntity())

    suspend fun getAll(): List<SessionRecord> =
        dao.getAllDescending().map { it.toDomain() }
}