package com.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.workouttracker.db.AppDatabase
import com.workouttracker.db.TimerRepository
import com.workouttracker.db.WorkoutRepository
import com.workouttracker.db.model.*
import com.workouttracker.ui.AppNavHost
import com.workouttracker.ui.theme.WorkoutTrackerTheme
import com.workouttracker.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var timerViewModel: TimerViewModel

    // Method to clear all data in the database
    private fun clearDatabase() {
        lifecycleScope.launch {
            try {
                // Delete all workouts
                workoutViewModel.deleteAll()

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
                workoutViewModel.getAllWorkout { existingWorkouts ->
                    if (existingWorkouts.isNotEmpty()) return@getAllWorkout // 🔹 If there are workouts, exit

                    // Create a new workout if the database is empty
                    val newWorkout = Workout(name = "LegDay", isSaved = false, date = Date())
                    val squat = Exercise(name = "Squat", type = "Leg")
                    val deadlift = Exercise(name = "Deadlift", type = "Leg")

                    workoutViewModel.addExercise(squat) { exerciseId ->
                        val workoutExercise1 = WorkoutExercise(workoutId = 0, exerciseId = exerciseId.toInt())

                        workoutViewModel.addExercise(deadlift) { exerciseId2 ->
                            val workoutExercise2 = WorkoutExercise(workoutId = 0, exerciseId = exerciseId2.toInt())

                            // Create sets for the exercises
                            val sett1 = Sett(workoutExerciseId = 0, weight = 90.0.toFloat(), reps = 5)
                            val sett2 = Sett(workoutExerciseId = 0, weight = 100.0.toFloat(), reps = 5)
                            val sett3 = Sett(workoutExerciseId = 0, weight = 120.0.toFloat(), reps = 3)

                            // Build the full WorkoutWithExercises object
                            val workoutWithExercises = WorkoutWithExercises(
                                workout = newWorkout,
                                workoutExercises = listOf(
                                    ExerciseWithSetts(workoutExercise1, squat, listOf(sett1, sett2)),
                                    ExerciseWithSetts(workoutExercise2, deadlift, listOf(sett3))
                                )
                            )

                            // Save it to the database
                            workoutViewModel.addWorkoutWithExercises(workoutWithExercises) {}
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
            AppDatabase::class.java,
            "workout-db"
        ).fallbackToDestructiveMigration().build()

        val workoutRepository = WorkoutRepository(database.workoutDao())
        val workoutFactory = WorkoutViewModelFactory(workoutRepository)
        workoutViewModel = ViewModelProvider(this, workoutFactory)[WorkoutViewModel::class.java]

        val timerRepository = TimerRepository(database.timerDao())
        val timerFactory = TimerViewModelFactory(timerRepository)
        timerViewModel = ViewModelProvider(this, timerFactory)[TimerViewModel::class.java]

        // Clear the database every time the app starts (for development)
        //clearDatabase()

        // Populate the database if it's empty
        //populateDatabaseIfEmpty()

        setContent {
            WorkoutTrackerTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, workoutViewModel = workoutViewModel, timerViewModel = timerViewModel)
            }
        }
    }
}
