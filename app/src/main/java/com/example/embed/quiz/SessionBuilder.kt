package com.example.embed.quiz

import com.example.embed.data.Question
import com.example.embed.srs.CardState

class SessionBuilder(
    private val allQuestions: List<Question>,
    private val cardStates: List<CardState>,
    private val sessionLength: Int,
    private val now: Long = System.currentTimeMillis()
) {
    fun buildSession(): List<Question> {
        val seenIds: Set<Int> = cardStates.map { it.questionId }.toSet()
        val dueIds: Set<Int> = cardStates.filter { it.dueDate <= now }.map { it.questionId }.toSet()

        val dueQuestions: List<Question> = allQuestions.filter { it.id in dueIds }
        val newQuestions: List<Question> = allQuestions.filter { it.id !in seenIds }
        val oldQuestions: List<Question> = allQuestions.filter { it.id in seenIds && it.id !in dueIds }

        val chosenQuestions = (dueQuestions + newQuestions + oldQuestions).take(sessionLength)
        return chosenQuestions.shuffled()
    }
}