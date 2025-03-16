package com.workouttracker.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.NumberPicker
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.workouttracker.sound.SoundManager
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
@Composable
fun TimerScreen(context: Context) {
    var timers by remember { mutableStateOf(listOf(5, 4)) }
    var currentTimers by remember { mutableStateOf(timers.toMutableList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(0) }
    var selectedSeconds by remember { mutableStateOf(30) }
    val soundManager = remember { SoundManager(context) }

    val listState = rememberLazyListState()

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (currentIndex < currentTimers.size && currentTimers[currentIndex] > 0) {
                delay(1000L)
                currentTimers = currentTimers.toMutableList().also {
                    it[currentIndex] = it[currentIndex] - 1
                }
            } else {
                // Play sound only when timer reaches 0
                if (currentIndex < currentTimers.size && currentTimers[currentIndex] == 0) {
                    soundManager.playAlertSound()
                }
                currentTimers = timers.toMutableList()
                // Move to the next timer, if there is one
                currentIndex = (currentIndex + 1) % currentTimers.size
            }
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Timer")
            }
        },
        bottomBar = {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { isRunning = !isRunning }) {
                    Text(if (isRunning) "Pause" else "Start")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    currentIndex = 0
                    currentTimers = timers.toMutableList()
                    isRunning = false
                }) {
                    Text("Reset")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Background Countdown Arc
            CountdownArc(
                totalTime = timers[currentIndex],
                currentTime = currentTimers[currentIndex],
                modifier = Modifier
                    .size(250.dp) // Adjust size as needed
                    .align(Alignment.Center)
            )

            // Centered Timer Display
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TimePickerRow(
                    time = currentTimers[currentIndex],
                    onTimeChange = { newTime ->
                        val updatedTimers = timers.toMutableList()
                        updatedTimers[currentIndex] = newTime
                        timers = updatedTimers
                        currentTimers = timers.toMutableList()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Timer Navigation Buttons
                Row {
                    Button(onClick = {
                        if (currentIndex > 0) currentIndex -= 1
                        else currentIndex = timers.size - 1
                    }) {
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        if (currentIndex < timers.size - 1) currentIndex += 1
                        else currentIndex = 0
                    }) {
                        Text("Next")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Timer") },
            text = {
                Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minutes")
                        AndroidView(
                            factory = { ctx ->
                                NumberPicker(ctx).apply {
                                    minValue = 0
                                    maxValue = 59
                                    value = selectedMinutes
                                    setOnValueChangedListener { _, _, newVal -> selectedMinutes = newVal }
                                }
                            }
                        )

                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Seconds")
                        AndroidView(
                            factory = { ctx ->
                                NumberPicker(ctx).apply {
                                    minValue = 0
                                    maxValue = 59
                                    value = selectedSeconds
                                    setOnValueChangedListener { _, _, newVal -> selectedSeconds = newVal }
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    timers = timers + (selectedMinutes * 60 + selectedSeconds)
                    currentTimers = timers.toMutableList()
                    showDialog = false
                }) {
                    Text("OK")
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

@SuppressLint("DefaultLocale")
@Composable
fun TimePickerRow(time: Int, onTimeChange: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(0) }
    var selectedSeconds by remember { mutableStateOf(30) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${time / 60}:${String.format("%02d", time % 60)}",
            style = MaterialTheme.typography.headlineLarge
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Timer") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minutes")
                        AndroidView(
                            factory = { ctx ->
                                NumberPicker(ctx).apply {
                                    minValue = 0
                                    maxValue = 59
                                    value = selectedMinutes
                                    setOnValueChangedListener { _, _, newVal -> selectedMinutes = newVal }
                                }
                            }
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Seconds")
                        AndroidView(
                            factory = { ctx ->
                                NumberPicker(ctx).apply {
                                    minValue = 0
                                    maxValue = 59
                                    value = selectedSeconds
                                    setOnValueChangedListener { _, _, newVal -> selectedSeconds = newVal }
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(selectedSeconds+selectedMinutes*60)
                    showDialog = false
                }) {
                    Text("OK")
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

@Composable
fun CountdownArc(
    totalTime: Int,
    currentTime: Int,
    modifier: Modifier
) {
    val animatedSweep = remember { Animatable(360f) }

    LaunchedEffect(currentTime) {
        val progress = (currentTime.toFloat() / totalTime) * 360f
        animatedSweep.animateTo(progress, animationSpec = tween(durationMillis = 500))
    }

    Canvas(modifier = modifier.size(150.dp)) {
        drawArc(
            color = Color.Blue,
            startAngle = -90f,
            sweepAngle = animatedSweep.value,
            useCenter = false,
            style = Stroke(width = 12f, cap = StrokeCap.Round)
        )
    }
}
