package com.workouttracker.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.workouttracker.sound.SoundManager
import com.workouttracker.viewmodel.TimerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerRunScreen(
    viewModel: TimerViewModel,
    context: Context
) {
    val soundManager = remember { SoundManager(context) }
    val timerState by viewModel.timerState.collectAsState()

    // A timer folyamatosan csökkenti az időt, ha fut
    LaunchedEffect(timerState.isRunning, timerState.remainingTime) {
        if (timerState.isRunning && timerState.remainingTime > 0) {
            delay(1000L)
            viewModel.decrementTimer()
        } else if (timerState.isRunning && timerState.remainingTime == 0) {
            viewModel.nextInterval()
            soundManager.playAlertSound()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Running Timer") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = timerState.currentIntervalType, style = MaterialTheme.typography.headlineLarge)
            Text(text = "${timerState.remainingTime} s", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = { viewModel.toggleTimer() }) {
                    Text(if (timerState.isRunning) "Pause" else "Start")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.resetTimer() }) {
                    Text("Reset")
                }
            }
        }
    }
}

