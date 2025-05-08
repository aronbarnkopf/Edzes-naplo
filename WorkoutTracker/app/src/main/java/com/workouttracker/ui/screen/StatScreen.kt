package com.workouttracker.ui.screen

import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.workouttracker.db.model.Exercise
import java.util.Date
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.workouttracker.db.model.Sett
import com.workouttracker.db.model.WorkoutWithExercises
import com.workouttracker.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StatScreen(viewModel: WorkoutViewModel){
    var exercises by remember { mutableStateOf(listOf<Exercise>()) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var chartData by remember { mutableStateOf(listOf<Pair<Date, List<Sett>>>()) }

    LaunchedEffect(Unit) {
        viewModel.getAllExercises { allExercises-> exercises = allExercises }
    }

    LaunchedEffect(selectedExercise) {
        chartData = emptyList()
        selectedExercise?.let { exercise ->
            viewModel.getWorkoutWithExercise(exercise.id) { exerciseList ->
                chartData = extractSettsForExercise(exerciseList, selectedExercise!!.id).toList()
            }
        } ?: run {
            chartData = emptyList()
        }
    }

    Scaffold {
        paddingValues ->
        Column (modifier = Modifier.padding(paddingValues).fillMaxSize()){
            var expanded by remember { mutableStateOf(false) }
            Button(
                onClick = {expanded = !expanded},
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                colors = ButtonColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent, contentColor = androidx.compose.ui.graphics.Color.Black,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Gray,
                    disabledContentColor = androidx.compose.ui.graphics.Color.Black
                )
            ){
                Text(style = TextStyle(fontSize = 24.sp),text = selectedExercise?.name ?: "Exercises")
                DropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
                    exercises.forEach {
                        DropdownMenuItem(
                            onClick = {
                                selectedExercise = it
                                expanded = false
                            },
                            text = {
                                Text(text = it.name ?: "")
                            }
                        )
                    }
                }
            }

            if (chartData.isNotEmpty()) {
                LineChartView(chartData)
                ChartDataList(chartData)
            } else {
                Text("No data for the chosen exercise.")
            }
        }
    }
}

@Composable
fun LineChartView(chartData: List<Pair<Date, List<Sett>>>) {
    Box(modifier = Modifier.fillMaxWidth().height(500.dp)) {
        AndroidView(factory = { context ->
            LineChart(context).apply {
                this.description.isEnabled = false
                this.setTouchEnabled(true)
                this.setPinchZoom(true)

                val entries = chartData.mapNotNull { (date, setts) ->
                    if (setts.isNotEmpty()) {
                        val maxSett = setts.maxByOrNull { it.weight }
                        maxSett?.let {
                            Entry(date.time.toFloat(), it.weight)
                        }
                    } else {
                        null
                    }
                }
                println("Last entry: ${entries.lastOrNull()}")
                val dataSet = LineDataSet(entries, "Progress").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                    setDrawCircles(true)
                    setDrawValues(false)
                }
                xAxis.valueFormatter = DateValueFormatter()
                xAxis.setDrawGridLines(false)
                this.data = LineData(dataSet)
                this.invalidate()
            }
        }, modifier = Modifier.fillMaxSize())
    }
}

fun extractSettsForExercise(
    workouts: List<WorkoutWithExercises>,
    exerciseId: Int
): List<Pair<Date, List<Sett>>> {
    return workouts.mapNotNull { workoutWithExercises ->
        val matchingExercise = workoutWithExercises.workoutExercises
            .find { it.exercise.id == exerciseId }

        matchingExercise?.setts
            ?.takeIf { it.isNotEmpty() }
            ?.let { setts ->
                workoutWithExercises.workout.date to setts
            }
    }
}

class DateValueFormatter : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return dateFormat.format(Date(value.toLong()))
    }
}

@Composable
fun ChartDataList(chartData: List<Pair<Date, List<Sett>>>) {
    LazyColumn {
        items(chartData) { data ->
            var expanded by remember { mutableStateOf(false) }
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data.first)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = !expanded },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        data.second.forEach { sett ->
                            Text(text = "Weight: ${sett.weight}, Reps: ${sett.reps}")
                        }
                    }
                }
            }
        }
    }
}