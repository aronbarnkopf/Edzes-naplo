package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutLogScreen(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edzésnapló", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate("new_workout") }) {
            Text("Új edzés rögzítése")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewWorkoutLogScreen() {
    WorkoutLogScreen(onNavigate = {})
}