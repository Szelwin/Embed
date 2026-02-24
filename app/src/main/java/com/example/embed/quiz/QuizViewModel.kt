package com.example.embed.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.embed.data.Question
import com.example.embed.data.SessionRecord
import com.example.embed.db.CardRepository
import com.example.embed.srs.CardState
import com.example.embed.srs.SpacedRepetitionEngine
import kotlinx.coroutines.launch

class QuizViewModel(
    allQuestions: List<Question>,
    sessionLength: Int = allQuestions.size,
    private val cardRepository: CardRepository? = null
) : ViewModel() {

    var questions by mutableStateOf<List<Question>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    var currentIndex by mutableIntStateOf(0)
        private set

    var selectedAnswerIndex by mutableIntStateOf(-1)
        private set

    var answered by mutableStateOf(false)
        private set

    var score by mutableIntStateOf(0)
        private set

    var streak by mutableIntStateOf(0)
        private set

    var highestStreak by mutableIntStateOf(0)
        private set

    var correctAnswers by mutableIntStateOf(0)
        private set

    val isFinished: Boolean
        get() = !loading && currentIndex >= questions.size

    init {
        if (cardRepository != null) {
            viewModelScope.launch {
                val cardStates = cardRepository.loadAll()
                questions = SessionBuilder(allQuestions, cardStates, sessionLength).buildSession()
                loading = false
            }
        } else {
            questions = allQuestions
            loading = false
        }
    }

    fun currentQuestion(): Question? =
        questions.getOrNull(currentIndex)

    // Step 1 of 2: user taps an answer card.
    // Records the selection and scores it, but does NOT advance yet.
    fun submitAnswer(selectedIndex: Int) {
        // Ignore submitting after question is already answered
        if (answered) return

        selectedAnswerIndex = selectedIndex
        answered = true

        val question = currentQuestion() ?: return
        val correct = selectedIndex == question.correctIndex

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

        if (cardRepository != null) {
            viewModelScope.launch {
                val existing = cardRepository.load(question.id)
                val card = existing ?: CardState(questionId = question.id)
                val updated = if (correct) {
                    SpacedRepetitionEngine.onCorrect(card)
                } else {
                    SpacedRepetitionEngine.onWrong(card)
                }
                cardRepository.save(updated)
            }
        }
    }

    // Step 2 of 2: user taps Next.
    // Resets per-question state and moves forward.
    fun advance() {
        selectedAnswerIndex = -1
        answered = false
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
    private val allQuestions: List<Question>,
    private val sessionLength: Int,
    private val cardRepository: CardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuizViewModel(allQuestions, sessionLength, cardRepository) as T
    }
}