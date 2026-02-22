package com.example.embed.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.embed.srs.CardState

@Entity(tableName = "card_states")
data class CardStateEntity(
    @PrimaryKey val questionId: Int,
    val interval: Int,
    val easeFactor: Float,
    val dueDate: Long
)

/**
*   Those functions convert to/from the [CardState] domain model we use in the app.
*   The [CardStateEntity] class is only used for the database part for persistence.
* */

fun CardStateEntity.toDomain() = CardState(
    questionId = questionId,
    interval = interval,
    easeFactor = easeFactor,
    dueDate = dueDate
)

fun CardState.toEntity() = CardStateEntity(
    questionId = questionId,
    interval   = interval,
    easeFactor = easeFactor,
    dueDate    = dueDate
)