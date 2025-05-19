package com.workouttracker.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.widget.NumberPicker
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.workouttracker.sound.SoundManager
import com.workouttracker.ui.theme.DarkGrayBlue
import com.workouttracker.ui.theme.DarkNeonBlue
import com.workouttracker.ui.theme.NeonBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun TimerScreen(context: Context) {
    var timers by remember { mutableStateOf(listOf(30)) }
    var currentTimers by remember { mutableStateOf(timers.toMutableList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableIntStateOf(0) }
    var selectedSeconds by remember { mutableIntStateOf(30) }
    val soundManager = remember { SoundManager(context) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(currentIndex) {
        coroutineScope.launch {
            listState.animateScrollToItem(currentIndex+1, -Resources.getSystem().getDisplayMetrics().heightPixels/2+500)
        }
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            coroutineScope.launch {
                listState.animateScrollToItem(currentIndex+1, -Resources.getSystem().getDisplayMetrics().heightPixels/2+500)
            }
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
        bottomBar = {
            BottomAppBar (containerColor = Color.Transparent) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier.width(150.dp)) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { isRunning = !isRunning },
                            colors = ButtonColors(
                                containerColor = DarkGrayBlue,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text(if (isRunning) "Pause" else "Start", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    Box(modifier = Modifier.width(150.dp)){
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentIndex = 0
                                currentTimers = timers.toMutableList()
                                isRunning = false
                            },
                            colors = ButtonColors(
                                containerColor = DarkGrayBlue,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Black
                            )
                        ) {
                            Text("Reset", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
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
                    .size(200.dp) // Adjust size as needed
                    .align(Alignment.Center)
            )
            Box(Modifier.align(alignment = Alignment.Center)){
                LazyColumn (state = listState, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    item {Spacer(modifier = Modifier.height(1000.dp))}
                    itemsIndexed(timers) { index, time ->
                        TimePickerRow(
                            time = currentTimers[index],
                            onTimeChange = { newTime ->
                                val updatedTimers = timers.toMutableList()
                                updatedTimers[index] = newTime
                                timers = updatedTimers
                                currentTimers[index] = newTime
                            },
                            style = if(index == currentIndex)TextStyle(fontSize = 48.sp, color = DarkNeonBlue) else TextStyle(fontSize = 36.sp),
                            modifier = if(index == currentIndex) Modifier.padding(100.dp) else Modifier.padding(8.dp),
                            onDelete = {
                                if(timers.size > 1){
                                    val updatedTimers = timers.toMutableList()
                                    updatedTimers.removeAt(index)
                                    timers = updatedTimers
                                    currentTimers = updatedTimers.toMutableList()
                                    if (currentIndex >= index) {
                                        currentIndex = (currentIndex - 1).coerceAtLeast(0)
                                    }
                                }
                            }
                        )
                    }
                    item { Button(colors = ButtonColors(
                        containerColor = Color.Transparent, contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black
                    ), onClick = { val updatedTimers = timers.toMutableList(); updatedTimers.add(30); timers = updatedTimers; currentTimers = timers.toMutableList() })
                    {
                        Icon(Icons.Filled.Add, contentDescription = "Add Timer", modifier = Modifier.size(36.dp))
                    } }
                    item {Spacer(modifier = Modifier.height(1000.dp))}
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
fun TimePickerRow(time: Int, onTimeChange: (Int) -> Unit, style: TextStyle, modifier: Modifier, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(0) }
    var selectedSeconds by remember { mutableStateOf(30) }

    Box(
        modifier = modifier
            .clickable { showDialog = true }
    ) {
        Text(
            "${time / 60}:${String.format("%02d", time % 60)}",
            style = style
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
                Row {
                    TextButton(onClick = { onDelete(); showDialog = false  }) {
                        Text("Delete")
                    }
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
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
            color = NeonBlue,
            startAngle = -90f,
            sweepAngle = animatedSweep.value,
            useCenter = false,
            style = Stroke(width = 36f, cap = StrokeCap.Round)
        )
    }
}

