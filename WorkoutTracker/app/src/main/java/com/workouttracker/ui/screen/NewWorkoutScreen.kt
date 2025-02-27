package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NewWorkoutScreen(onSave: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Új edzés rögzítése", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Gyakorlat neve") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onSave) {
            Text("Mentés")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewNewWorkoutScreen() {
    NewWorkoutScreen(onSave = {})
}