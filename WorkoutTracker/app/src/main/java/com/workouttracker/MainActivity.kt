package com.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.workouttracker.db.WorkoutDatabase
import com.workouttracker.db.WorkoutRepository
import com.workouttracker.db.model.*
import com.workouttracker.ui.WorkoutAppNavHost
import com.workouttracker.ui.theme.WorkoutTrackerTheme
import com.workouttracker.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: WorkoutViewModel

    // Method to clear all data in the database
    private fun clearDatabase() {
        lifecycleScope.launch {
            try {
                // Delete all workouts
                viewModel.deleteAll()

                // Log the success (optional)
                println("Database cleared!")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Populating database if empty
    private fun populateDatabaseIfEmpty() {
        lifecycleScope.launch {
            try {
                viewModel.getAllWorkout { existingWorkouts ->
                    if (existingWorkouts.isNotEmpty()) return@getAllWorkout // 🔹 If there are workouts, exit

                    // Create a new workout if the database is empty
                    val newWorkout = Workout(name = "LegDay", isSaved = false, date = Date())
                    val squat = Exercise(name = "Squat", type = "Leg")
                    val deadlift = Exercise(name = "Deadlift", type = "Leg")

                    viewModel.addExercise(squat) { exerciseId ->
                        val workoutExercise1 = WorkoutExercise(workoutId = 0, exerciseId = exerciseId.toInt())

                        viewModel.addExercise(deadlift) { exerciseId2 ->
                            val workoutExercise2 = WorkoutExercise(workoutId = 0, exerciseId = exerciseId2.toInt())

                            // Create sets for the exercises
                            val sett1 = Sett(workoutExerciseId = 0, weight = 90.0, reps = 5)
                            val sett2 = Sett(workoutExerciseId = 0, weight = 100.0, reps = 5)
                            val sett3 = Sett(workoutExerciseId = 0, weight = 120.0, reps = 3)

                            // Build the full WorkoutWithExercises object
                            val workoutWithExercises = WorkoutWithExercises(
                                workout = newWorkout,
                                workoutExercises = listOf(
                                    ExerciseWithSetts(workoutExercise1, squat, listOf(sett1, sett2)),
                                    ExerciseWithSetts(workoutExercise2, deadlift, listOf(sett3))
                                )
                            )

                            // Save it to the database
                            viewModel.addWorkoutWithExercises(workoutWithExercises) {}
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "workout-db"
        ).fallbackToDestructiveMigration().build()

        val repository = WorkoutRepository(database.workoutDao())
        val factory = WorkoutViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        // Clear the database every time the app starts (for development)
        //clearDatabase()

        // Populate the database if it's empty
        //populateDatabaseIfEmpty()

        setContent {
            WorkoutTrackerTheme {
                val navController = rememberNavController()
                WorkoutAppNavHost(navController = navController, viewModel = viewModel)
            }
        }
    }
}
