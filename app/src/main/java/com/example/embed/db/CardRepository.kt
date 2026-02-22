package com.example.embed.db

import com.example.embed.srs.CardState

class CardRepository(private val dao: CardStateDao) {
    suspend fun save(card: CardState) =
        dao.upsert(card.toEntity())

    suspend fun load(questionId: Int): CardState? =
        dao.getById(questionId)?.toDomain()

    suspend fun loadAll(): List<CardState> =
        dao.getAll().map { it.toDomain() }
}