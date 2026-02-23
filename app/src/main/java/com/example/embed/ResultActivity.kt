package com.example.embed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.embed.data.QuestionBank
import com.example.embed.data.SessionRecord
import com.example.embed.quiz.QuizViewModel
import com.example.embed.ui.theme.EmbedTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.embed.db.AppDatabase
import com.example.embed.db.SessionRepository

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val record = intent.getSerializableExtra("record") as SessionRecord

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(this@ResultActivity).sessionDao()
            val repo = SessionRepository(dao)
            repo.save(record)
        }

        val score = record.score
        val accuracy = record.accuracyPercent
        val highestStreak = record.highestStreak

        setContent {
            EmbedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResultScreen(
                        score = score,
                        accuracy = accuracy,
                        highestStreak = highestStreak,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ResultScreen(
    score: Int,
    accuracy: Int,
    highestStreak: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Results",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Stats rows
        StatRow(label = "Score:", value = "$score points")
        StatRow(label = "Accuracy:", value = "$accuracy %")
        StatRow(label = "Highest streak:", value = "$highestStreak questions")

        Spacer(modifier = Modifier.height(48.dp))

        // Navigation buttons side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, QuizActivity::class.java))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Retry")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Home")
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResultActivityPreview() {
    val score = 45
    val accuracy = 90
    val highestStreak = 8

    EmbedTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ResultScreen(
                score = score,
                accuracy = accuracy,
                highestStreak = highestStreak,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}