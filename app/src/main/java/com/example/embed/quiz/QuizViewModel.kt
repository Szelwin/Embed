package com.example.embed.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.embed.data.Question
import com.example.embed.data.SessionRecord

class QuizViewModel(
    private val questions: List<Question>
) : ViewModel() {
    private var currentIndex = 0
    var score = 0
        private set
    var streak = 0
        private set
    var highestStreak = 0
        private set
    var correctAnswers = 0
        private set

    val isFinished: Boolean
        get() = currentIndex >= questions.size

    fun currentQuestion(): Question? =
        questions.getOrNull(currentIndex)

    fun submitAnswer(selectedIndex: Int) {
        val correctIndex = currentQuestion()?.correctIndex
        val correct = selectedIndex == correctIndex

        if (correct) {
            score += 10
            streak += 1
            correctAnswers += 1
            highestStreak = maxOf(highestStreak, streak)
        }

        else {
            score = maxOf(0, score - 5)
            streak = 0
        }

        currentIndex += 1
    }

    fun toSessionRecord(): SessionRecord {
        return SessionRecord(
            timestamp = System.currentTimeMillis(),
            score = score,
            highestStreak = highestStreak,
            totalQuestions = questions.size,
            correctAnswers = correctAnswers
        )
    }
}

class QuizViewModelFactory(
    private val questions: List<Question>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuizViewModel(questions) as T
    }
}