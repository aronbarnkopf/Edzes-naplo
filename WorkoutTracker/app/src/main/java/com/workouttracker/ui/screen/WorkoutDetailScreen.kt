package com.workouttracker.ui.screen

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

@Composable
fun WorkoutDetailScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    // Get the temporary workout from the ViewModel
    val workoutWithExercises = viewModel.temporaryWorkoutWithExercises.collectAsState().value
        ?: return // Return early if the workout is null

    Scaffold(
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
                    Text(exerciseWithSetts.exercise.name, style = MaterialTheme.typography.titleMedium)
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
        }
    }
}
