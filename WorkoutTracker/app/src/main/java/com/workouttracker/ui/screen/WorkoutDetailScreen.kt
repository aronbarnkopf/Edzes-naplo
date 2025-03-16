package com.workouttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.workouttracker.db.model.*
import com.workouttracker.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val workoutWithExercises = viewModel.temporaryWorkoutWithExercises.collectAsState().value ?: return
    val availableExercises = viewModel.availableExercises.collectAsState().value
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workoutWithExercises.workout.name) },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Workout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.addWorkoutWithExercises(workoutWithExercises) {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Save Workout")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(workoutWithExercises.workoutExercises) { exerciseWithSetts ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(exerciseWithSetts.exercise.name, style = MaterialTheme.typography.titleMedium)
                        if (isEditing) {
                            IconButton(onClick = {
                                viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                    workoutExercises = workoutWithExercises.workoutExercises.filterNot { it == exerciseWithSetts }
                                ))
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Exercise")
                            }
                        }
                    }
                    exerciseWithSetts.setts.forEachIndexed { index, sett ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextField(
                                value = sett.weight.toString(),
                                onValueChange = { inputValue ->
                                    viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                        workoutExercises = workoutWithExercises.workoutExercises.map { exercise ->
                                            if (exercise == exerciseWithSetts) {
                                                exercise.copy(setts = exercise.setts.toMutableList().apply {
                                                    set(index, sett.copy(weight = inputValue.toDoubleOrNull() ?: sett.weight))
                                                })
                                            } else exercise
                                        }
                                    ))
                                },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = sett.reps.toString(),
                                onValueChange = { inputValue ->
                                    viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                        workoutExercises = workoutWithExercises.workoutExercises.map { exercise ->
                                            if (exercise == exerciseWithSetts) {
                                                exercise.copy(setts = exercise.setts.toMutableList().apply {
                                                    set(index, sett.copy(reps = inputValue.toIntOrNull() ?: sett.reps))
                                                })
                                            } else exercise
                                        }
                                    ))
                                },
                                label = { Text("Reps") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(onClick = {
                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                            workoutExercises = workoutWithExercises.workoutExercises.map {
                                if (it == exerciseWithSetts) {
                                    it.copy(setts = it.setts + Sett(workoutExerciseId = it.workoutExercise.id, weight = 0.0, reps = 0))
                                } else it
                            }
                        ))
                    }) {
                        Text("Add Set")
                    }
                }
            }
            if (isEditing) {
                item {
                    Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add Exercise")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Exercise")
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Exercise") },
            text = {
                var expanded by remember { mutableStateOf(false) }

                Box {
                    Button(onClick = { expanded = true }) {
                        Text(selectedExercise?.name ?: "Select Exercise")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableExercises.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise.name) },
                                onClick = {
                                    selectedExercise = exercise
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedExercise?.let { exercise ->
                        val newExerciseWithSetts = ExerciseWithSetts(
                            exercise = exercise,
                            setts = emptyList(),
                            workoutExercise = WorkoutExercise(id = 0, workoutId = workoutWithExercises.workout.id, exerciseId = exercise.id)
                        )
                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                            workoutExercises = workoutWithExercises.workoutExercises + newExerciseWithSetts
                        ))
                    }
                    showDialog = false
                    selectedExercise = null
                }) {
                    Text("Add")
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
