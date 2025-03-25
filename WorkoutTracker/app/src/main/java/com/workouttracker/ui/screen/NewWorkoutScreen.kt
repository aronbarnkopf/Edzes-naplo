package com.workouttracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun NewWorkoutScreen(viewModel: WorkoutViewModel, onNavigate: (String) -> Unit) {
    var workoutName by remember { mutableStateOf("") }
    val availableExercises by viewModel.availableExercises.collectAsState(initial = listOf())
    var selectedExercises by remember { mutableStateOf(listOf<Exercise>()) }

    // Dialog állapot
    var isDialogOpen by remember { mutableStateOf(false) }
    var newExerciseName by remember { mutableStateOf("") }
    var newExerciseType by remember { mutableStateOf("") }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("New Workout") })
        },
        floatingActionButton = {
            Button(
                onClick = {
                    val workoutWithExercises = WorkoutWithExercises(
                        workout = Workout(name = workoutName, isSaved = true, date = Date()),
                        workoutExercises = selectedExercises.map { exercise ->
                            ExerciseWithSetts(
                                workoutExercise = WorkoutExercise(workoutId = 0, exerciseId = exercise.id),
                                exercise = exercise,
                                setts = emptyList()
                            )
                        }
                    )
                    viewModel.addWorkoutWithExercises(workoutWithExercises) {}
                    onNavigate("workout_log")
                },
                enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty(),
                colors = ButtonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Text("Save", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(selectedExercises) { exercise ->
                    Button(
                        onClick = { isDialogOpen = true; selectedExercise = exercise },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGrayBlue, contentColor = Color.Black),
                    ) {
                        Text(text = exercise.name)
                    }
                }
            }

            Button(
                onClick = { isDialogOpen = true; selectedExercise = null },
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGrayBlue, contentColor = Color.Black),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    }

    // **Dialog a gyakorlat hozzáadásához**
    if (isDialogOpen) {
        var expandedExercise by remember { mutableStateOf(false) }
        var expandedType by remember { mutableStateOf(false) }

        AlertDialog(
            containerColor = LightGrayBlue,
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Select or Add Exercise") },
            text = {
                Column {
                    // **Meglévő gyakorlat kiválasztása**
                    Box {
                        Button(
                            onClick = { expandedExercise = true },
                            colors = ButtonColors(
                                containerColor = DarkGrayBlue,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Gray
                            )
                        ) {
                            Text(text = selectedExercise?.name ?: "Select Exercise")
                        }
                        DropdownMenu(expanded = expandedExercise, onDismissRequest = { expandedExercise = false }) {
                            availableExercises.forEach { exercise ->
                                DropdownMenuItem(
                                    text = { Text(exercise.name) },
                                    onClick = {
                                        selectedExercises = selectedExercises + exercise
                                        expandedExercise = false
                                        isDialogOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Or create a new one:")

                    // **Új gyakorlat neve**
                    TextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // **Új gyakorlat típusa kiválasztás**
                    Box(Modifier.padding(16.dp)) {
                        Button(
                            onClick = { expandedType = !expandedType },
                            colors = ButtonColors(
                                containerColor = DarkGrayBlue,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.Gray
                            )
                        ) {
                            Text(newExerciseType.ifEmpty { "Select Type" })
                        }
                    }
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        listOf("Back", "Chest", "Shoulder", "Arm", "Leg").forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    newExerciseType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newExerciseName.isNotBlank()) {
                            val newExercise = Exercise(
                                name = newExerciseName,
                                id = 0, // Az adatbázis generálja az ID-t
                                type = newExerciseType
                            )
                            viewModel.addExercise(newExercise) { id ->
                                selectedExercises = selectedExercises + newExercise.copy(id = id.toInt())
                                isDialogOpen = false
                                newExerciseName = "" // Resetelés
                                newExerciseType = ""
                            }
                        }
                    },
                    colors = ButtonColors(
                        containerColor = DarkGrayBlue,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isDialogOpen = false },
                    colors = ButtonColors(
                        containerColor = DarkGrayBlue,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
