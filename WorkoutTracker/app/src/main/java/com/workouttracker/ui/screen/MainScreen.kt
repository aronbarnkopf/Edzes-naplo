package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(onNavigate: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Edzésnaplózó App", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onNavigate("workout_log") }) {
                Text("📓 Edzésnapló")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onNavigate("timer") }) {
                Text("⏱ Időzítő")
            }
        }
    }
}
