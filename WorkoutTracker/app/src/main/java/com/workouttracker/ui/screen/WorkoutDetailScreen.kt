package com.workouttracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.workouttracker.db.model.*
import com.workouttracker.ui.theme.DarkGrayBlue
import com.workouttracker.ui.theme.LightGrayBlue
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
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(workoutWithExercises.workout.name, style = MaterialTheme.typography.headlineMedium) },
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
                modifier = Modifier.padding(16.dp),
                containerColor = Color.LightGray
            ) {
                Text("Save", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(workoutWithExercises.workoutExercises) { exerciseWithSetts ->
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(exerciseWithSetts.exercise.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
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
                    Row (modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text("WEIGHT" , style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.weight(2f))
                        Text("REPS" , style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    exerciseWithSetts.setts.forEachIndexed { index, sett ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.weight(1f))
                            var weightFieldValue by remember { mutableStateOf(TextFieldValue(sett.weight.toString())) }
                            val weightFocusRequester = remember { FocusRequester() }

                            TextField(
                                textStyle = TextStyle(textAlign = TextAlign.Center),
                                value = weightFieldValue,
                                onValueChange = { inputValue ->
                                    if (inputValue.text.matches(Regex("^\\d*\\.?\\d*\$"))) {
                                        weightFieldValue = inputValue
                                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                            workoutExercises = workoutWithExercises.workoutExercises.map { exercise ->
                                                if (exercise == exerciseWithSetts) {
                                                    exercise.copy(setts = exercise.setts.toMutableList().apply {
                                                        set(index, sett.copy(weight = inputValue.text.toFloatOrNull() ?: sett.weight))
                                                    })
                                                } else exercise
                                            }
                                        ))
                                    }
                                },
                                modifier = Modifier
                                    .focusRequester(weightFocusRequester)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            weightFieldValue = weightFieldValue.copy(
                                                selection = TextRange(0, weightFieldValue.text.length) // Kijelöli az egész szöveget
                                            )
                                        }
                                    }
                                    .weight(3f),
                                keyboardOptions = KeyboardOptions.Default,
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.weight(2f))
                            var repsFieldValue by remember { mutableStateOf(TextFieldValue(sett.reps.toString())) }
                            val repsFocusRequester = remember { FocusRequester() }

                            TextField(
                                textStyle = TextStyle(textAlign = TextAlign.Center),
                                value = repsFieldValue,
                                onValueChange = { inputValue ->
                                    if (inputValue.text.matches(Regex("^\\d*\$"))) { // Csak egész számokat enged be
                                        repsFieldValue = inputValue
                                        viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                            workoutExercises = workoutWithExercises.workoutExercises.map { exercise ->
                                                if (exercise == exerciseWithSetts) {
                                                    exercise.copy(setts = exercise.setts.toMutableList().apply {
                                                        set(index, sett.copy(reps = inputValue.text.toIntOrNull() ?: sett.reps))
                                                    })
                                                } else exercise
                                            }
                                        ))
                                    }
                                },
                                modifier = Modifier
                                    .focusRequester(repsFocusRequester)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            repsFieldValue = repsFieldValue.copy(
                                                selection = TextRange(0, repsFieldValue.text.length) // Kijelöli az egész szöveget
                                            )
                                        }
                                    }
                                    .weight(3f),
                                keyboardOptions = KeyboardOptions.Default,
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(
                        onClick = {
                            viewModel.setTemporaryWorkout(workoutWithExercises.copy(
                                workoutExercises = workoutWithExercises.workoutExercises.map {
                                    if (it == exerciseWithSetts) {
                                        it.copy(setts = it.setts + Sett(workoutExerciseId = it.workoutExercise.id, weight = 0.0.toFloat(), reps = 0))
                                    } else it
                                }
                            ))
                        },
                        shape = CircleShape,
                        colors = ButtonColors(
                            containerColor = DarkGrayBlue, contentColor = Color.Black,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Sett", modifier = Modifier.size(32.dp))
                    }
                }
            }
            if (isEditing) {
                item {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonColors(
                            containerColor = DarkGrayBlue, contentColor = Color.Black,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Black
                        )
                    ) {
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
            containerColor = LightGrayBlue,
            onDismissRequest = { showDialog = false },
            title = { Text("Select Exercise") },
            text = {
                var expanded by remember { mutableStateOf(false) }

                Box {
                    Button(onClick = { expanded = true }, colors = ButtonColors(
                        containerColor = DarkGrayBlue,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black
                    )
                    ) {
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
                TextButton(
                        onClick = {
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
                    },
                    colors = ButtonColors(
                        containerColor = DarkGrayBlue,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false },
                    colors = ButtonColors(
                        containerColor = DarkGrayBlue,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black)
                    ) {
                    Text("Cancel")
                }
            }
        )
    }
}
