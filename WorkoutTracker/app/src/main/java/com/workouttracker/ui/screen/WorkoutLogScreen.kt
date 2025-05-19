package com.workouttracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.workouttracker.db.model.*
import com.workouttracker.ui.theme.DarkGrayBlue
import com.workouttracker.ui.theme.LightGrayBlue
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
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Workouts", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Workout")
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onNavigate("new_workout")
                },
                modifier = Modifier.padding(16.dp),
                containerColor = Color.LightGray
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Workout", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                // Üres workout listaelem
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable{
                        viewModel.setTemporaryWorkout(emptyWorkout)
                        onNavigate("workout_detail")
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGrayBlue
                    )
                ) {
                    Text(emptyWorkout.workout.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                }
            }
            itemsIndexed(savedWorkouts) { index, workoutWithExercises ->
                // Display each saved workout as a button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable{
                        // Save a copy of the workout to the ViewModel's temporary state
                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(workout = workoutWithExercises.workout.copy(id = 0, date = Date(), isSaved = false),
                            workoutWithExercises.workoutExercises.map { it.copy(workoutExercise = it.workoutExercise.copy(id = 0)) }))
                        // Navigate to the workout detail screen
                        onNavigate("workout_detail")
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGrayBlue
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(workoutWithExercises.workout.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        if(isEditing)Button(onClick = {viewModel.removeWorkout(savedWorkouts[index].workout.id)}, colors = ButtonColors(
                            containerColor = Color.Transparent, contentColor = Color.Black,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Black
                        )) { Icon(Icons.Default.Delete, contentDescription = "Remove Template Exercise")}
                    }
                }
            }
        }
    }
}
