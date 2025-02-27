package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edzésnaplózó App", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigate("workout_log") }) {
            Text("Edzésnapló")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate("timer_setup") }) {
            Text("Időzítő")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PreviewMainScreen() {
    MainScreen(onNavigate = {})
}