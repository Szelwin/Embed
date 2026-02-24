package com.example.embed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.embed.settings.SettingsManager
import com.example.embed.ui.theme.EmbedTheme
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.embed.data.Domain

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsManager = SettingsManager(this)

        setContent {
            EmbedTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        settingsManager = settingsManager,
                        onSave = { finish() },
                        onGoBack = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onSave: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var questionsPerSession by remember {
        mutableIntStateOf(settingsManager.questionsPerSession)
    }
    var enabledDomains by remember {
        mutableStateOf(settingsManager.enabledDomains)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Questions per session:",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    questionsPerSession = (questionsPerSession - 1).coerceAtLeast(1)
                }) {
                    Text("-")
                }

                Text(
                    text = questionsPerSession.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(onClick = {
                    questionsPerSession = (questionsPerSession + 1).coerceAtMost(40)
                }) {
                    Text("+")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        ) {
            Text("Enabled domains:", style = MaterialTheme.typography.bodyLarge)
            for (domain in Domain.entries) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = domain.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Switch(
                        checked = domain in enabledDomains,
                        onCheckedChange = {
                            if (domain in enabledDomains && enabledDomains.size == 1) {
                                return@Switch  // don't allow disabling the last domain
                            }

                            enabledDomains = if (domain in enabledDomains) {
                                enabledDomains - domain
                            } else {
                                enabledDomains + domain
                            }
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    settingsManager.questionsPerSession = questionsPerSession
                    settingsManager.enabledDomains = enabledDomains
                    onSave()
                }) {
                Text("Save")
            }

            Button(
                onClick = onGoBack) {
                Text("Back")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsActivityPreview() {
    val context = LocalContext.current

    EmbedTheme {
        SettingsScreen(
            settingsManager = SettingsManager(context),
            onSave = {},
            onGoBack = {}
        )
    }
}