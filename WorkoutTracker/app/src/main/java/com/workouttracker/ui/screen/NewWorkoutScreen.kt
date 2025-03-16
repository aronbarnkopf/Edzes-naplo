package com.workouttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.workouttracker.db.model.*
import com.workouttracker.viewmodel.WorkoutViewModel
import java.util.Date

@Composable
fun NewWorkoutScreen(viewModel: WorkoutViewModel, onNavigate: (String) -> Unit) {
    var workoutName by remember { mutableStateOf("") }
    var selectedExercises by remember { mutableStateOf(listOf<Exercise?>()) }
    val availableExercises by viewModel.availableExercises.collectAsState(initial = listOf())

    // Dialog állapot
    var isDialogOpen by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }
    var newExerciseType by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            label = { Text("Workout Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Exercises")

        LazyColumn {
            items(selectedExercises) { selectedExercise ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        Button(onClick = { expanded = true }) {
                            Text(text = selectedExercise?.name ?: "Select Exercise")
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            availableExercises.forEach { exercise ->
                                DropdownMenuItem(
                                    text = { Text(exercise.name) },
                                    onClick = {
                                        selectedExercises = selectedExercises.map { e ->
                                            if (e == selectedExercise) exercise else e
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Az Add gomb, ami megnyitja a Dialog-ot
                    IconButton(onClick = { isDialogOpen = true }) {
                        Icon(Icons.Default.Add, contentDescription = "New Exercise")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(onClick = { selectedExercises = selectedExercises + null }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Add Exercise")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val savedWorkoutWithExercises = WorkoutWithExercises(
                    workout = Workout(name = workoutName, isSaved = true, date = Date()),
                    workoutExercises = selectedExercises.mapNotNull { it }.map { exercise ->
                        ExerciseWithSetts(
                            workoutExercise = WorkoutExercise(
                                workoutId = 0,
                                exerciseId = exercise.id
                            ),
                            exercise = exercise,
                            setts = emptyList()
                        )
                    }
                )
                viewModel.addWorkoutWithExercises(savedWorkoutWithExercises, onSuccess = {})
                onNavigate("workout_log")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = workoutName.isNotBlank() && selectedExercises.any { it != null }
        ) {
            Text("Save Workout")
        }
    }

    // Dialog a gyakorlat hozzáadásához
    if (isDialogOpen) {
        var expanded by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Add New Exercise") },
            text = {
                Column {
                    TextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(Modifier.padding(16.dp)) {
                        Button(
                            onClick = { expanded = !expanded }
                        ) {
                            Text(newExerciseType.ifEmpty { "Type" }) // A kiválasztott típus jelenjen meg
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Back", "Chest", "Shoulder", "Arm", "Leg").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    newExerciseType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Az új gyakorlat hozzáadása az adatbázishoz
                        val newExercise = Exercise(
                            name = newExerciseName,
                            id = 0, // Ez az id csak ideiglenes, mivel az adatbázisban generálódik
                            type = newExerciseType
                        )
                        viewModel.addExercise(newExercise) { id ->
                            // Miután hozzáadtuk, bezárjuk a Dialog-ot
                            isDialogOpen = false
                            newExerciseName = "" // Reseteljük a TextField-et

                            // Az új gyakorlat hozzáadása a kiválasztott gyakorlatok közé
                            selectedExercises = selectedExercises.map { e ->
                                e ?: newExercise.copy(id = id.toInt())
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { isDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
