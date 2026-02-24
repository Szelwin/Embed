package com.example.embed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.embed.data.SessionRecord
import com.example.embed.db.AppDatabase
import com.example.embed.db.SessionRepository
import com.example.embed.settings.SettingsManager
import com.example.embed.ui.theme.EmbedTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = SessionRepository(db.sessionDao())

        setContent {
            EmbedTheme {
                var sessions by remember { mutableStateOf<List<SessionRecord>>(emptyList()) }

                LaunchedEffect(Unit) {
                    sessions = repository.getAll()
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HistoryScreen(
                        sessions = sessions,
                        modifier = Modifier.padding(innerPadding),
                        onGoBack = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    sessions: List<SessionRecord>,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Score History",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (sessions.isEmpty()) {
            Text(
                text = "No sessions yet. Start a quiz!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("emptyMessage")
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.testTag("sessionList")
            ) {
                items(sessions) { session ->
                    SessionCard(session)
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = {
            onGoBack()
        }) {
            Text("Back")
        }
    }
}

@Composable
fun SessionCard(session: SessionRecord) {
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
    val dateText = formatter.format(Date(session.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sessionCard")
    ) {
        Text(
            text = "$dateText    ${session.score} points, ${session.accuracyPercent} %",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryActivityPreview() {
    val ts1 = Calendar.getInstance().apply {
        set(2026, Calendar.FEBRUARY, 24, 18, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val ts2 = Calendar.getInstance().apply {
        set(2026, Calendar.FEBRUARY, 24, 18, 10, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val sr1 = SessionRecord(
        timestamp = ts1,
        score = 45,
        highestStreak = 8,
        totalQuestions = 30,
        correctAnswers = 20
    )

    val sr2 = SessionRecord(
        timestamp = ts2,
        score = 30,
        highestStreak = 5,
        totalQuestions = 20,
        correctAnswers = 6
    )

    val sessions: List<SessionRecord> = listOf(sr1, sr2)

    EmbedTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            HistoryScreen(
                sessions = sessions,
                modifier = Modifier.padding(innerPadding),
                {}
            )
        }
    }
}