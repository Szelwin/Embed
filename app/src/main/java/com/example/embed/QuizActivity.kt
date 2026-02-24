package com.example.embed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.embed.data.Question
import com.example.embed.data.QuestionBank
import com.example.embed.db.AppDatabase
import com.example.embed.db.CardRepository
import com.example.embed.quiz.QuizViewModel
import com.example.embed.quiz.QuizViewModelFactory
import com.example.embed.settings.SettingsManager
import com.example.embed.ui.theme.EmbedTheme
import kotlin.getValue

enum class AnswerCardState { DEFAULT, CORRECT, WRONG }

class QuizActivity : ComponentActivity() {

    private val vm: QuizViewModel by viewModels {
        val settingsManager = SettingsManager(this)
        val enabledDomains = settingsManager.enabledDomains
        val questionCount = settingsManager.questionsPerSession

        val allQuestions = QuestionBank.all.filter { it.domain in enabledDomains }

        val db = AppDatabase.getInstance(this)
        val cardRepository = CardRepository(db.cardStateDao())

        QuizViewModelFactory(allQuestions, questionCount, cardRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmbedTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuizScreen(
                        vm = vm,
                        onSessionFinished = {
                            val record = vm.toSessionRecord()

                            val intent = Intent(this@QuizActivity, ResultActivity::class.java).apply {
                                putExtra("record", record)
                            }

                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizScreen(vm: QuizViewModel, onSessionFinished: () -> Unit) {
    if (vm.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val question = vm.currentQuestion() ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Question ${vm.currentIndex + 1}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        AnswerGrid(
            question = question,
            answered = vm.answered,
            selectedIndex = vm.selectedAnswerIndex,
            onAnswerSelected = { vm.submitAnswer(it) }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (vm.answered && vm.selectedAnswerIndex != question.correctIndex) {
                Text(
                    text = question.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (vm.answered) {
                Button(
                    onClick = {
                        vm.advance()
                        if (vm.isFinished) onSessionFinished()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun AnswerGrid(question: Question, answered: Boolean, selectedIndex: Int, onAnswerSelected: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in 0..1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0..1) {
                    val idx = row * 2 + col
                    AnswerCard(
                        text = question.options[idx],
                        cardState = when {
                            !answered                    -> AnswerCardState.DEFAULT
                            idx == question.correctIndex -> AnswerCardState.CORRECT
                            idx == selectedIndex         -> AnswerCardState.WRONG
                            else                         -> AnswerCardState.DEFAULT
                        },
                        onClick = { onAnswerSelected(idx) },
                        enabled = !answered,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerCard(text: String, cardState: AnswerCardState, onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    val containerColor = when (cardState) {
        AnswerCardState.CORRECT -> Color(0xFF4CAF50)
        AnswerCardState.WRONG   -> Color(0xFFF44336)
        AnswerCardState.DEFAULT -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (cardState) {
        AnswerCardState.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
        else                    -> Color.White
    }

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizScreenPreview() {
    val vm: QuizViewModel = QuizViewModel(QuestionBank.all.shuffled().take(3))

    EmbedTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            QuizScreen(
                vm = vm,
                onSessionFinished = {}
            )
        }
    }
}