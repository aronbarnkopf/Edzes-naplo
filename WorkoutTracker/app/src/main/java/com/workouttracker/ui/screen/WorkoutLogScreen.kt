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
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutLogScreen(
    viewModel: WorkoutViewModel,
    onNavigate: (String) -> Unit
) {
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()
    val emptyWorkout = WorkoutWithExercises(
        workout = Workout(id = 0, name = "Empty Workout", date = Date(), isSaved = false),
        workoutExercises = emptyList()
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Workout Log") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to the "workout_detail" screen for creating a new workout
                    onNavigate("new_workout")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Add Workout")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                // Üres workout listaelem
                Button(
                    onClick = {
                        viewModel.setTemporaryWorkout(emptyWorkout)
                        onNavigate("workout_detail")
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(emptyWorkout.workout.name)
                }
            }
            items(savedWorkouts) { workoutWithExercises ->
                // Display each saved workout as a button
                Button(
                    onClick = {
                        // Save a copy of the workout to the ViewModel's temporary state
                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(workout = workoutWithExercises.workout.copy(id = 0, date = Date(), isSaved = false),
                            workoutWithExercises.workoutExercises.map { it.copy(workoutExercise = it.workoutExercise.copy(id = 0)) }))
                        // Navigate to the workout detail screen
                        onNavigate("workout_detail")
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(workoutWithExercises.workout.name)
                }
            }
        }
    }
}
