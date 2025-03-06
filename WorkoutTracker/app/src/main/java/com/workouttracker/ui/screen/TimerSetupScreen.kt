package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimerSetupScreen(onStart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Időzítő beállítás", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Munkaintervallum (mp)") })
        TextField(value = "", onValueChange = {}, label = { Text("Pihenőidő (mp)") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onStart) {
            Text("Indítás")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewTimerSetupScreen() {
    TimerSetupScreen(onStart = {})
}