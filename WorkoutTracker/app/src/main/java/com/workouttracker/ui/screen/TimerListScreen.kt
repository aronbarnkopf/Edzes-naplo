package com.workouttracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.workouttracker.db.model.TimerInterval
import com.workouttracker.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerListScreen(
    viewModel: TimerViewModel,
    onNavigate: (String) -> Unit
) {
    val timerPresets by viewModel.timerPresets.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var timerName by remember { mutableStateOf("") }
    var repeatCount by remember { mutableStateOf("1") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Saved Timers") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Timer")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(timerPresets) { timer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { viewModel.setTimerState(timer.id, 0, false); onNavigate("timer_run") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = timer.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Repeat: ${timer.repeatCount}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    // Új időzítő hozzáadása dialógus
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; timerName = ""; repeatCount = "1" },
            title = { Text("Add New Timer") },
            text = {
                Column {
                    TextField(
                        value = timerName,
                        onValueChange = { timerName = it },
                        label = { Text("Timer Name") }
                    )
                    TextField(
                        value = repeatCount,
                        onValueChange = { repeatCount = it },
                        label = { Text("Repeat Count") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addTimerPreset(
                        timerName, repeatCount.toIntOrNull() ?: 1,
                        intervals = List<TimerInterval>(2) { TimerInterval(0, 0, 30, "work") }
                    )
                    timerName = ""
                    repeatCount = "1"
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

