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
import com.workouttracker.db.WorkoutRepository
import com.workouttracker.db.model.*
import com.workouttracker.ui.AppNavHost
import com.workouttracker.ui.theme.WorkoutTrackerTheme
import com.workouttracker.viewmodel.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var workoutViewModel: WorkoutViewModel

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


        // Clear the database every time the app starts (for development)
        //clearDatabase()


        setContent {
            WorkoutTrackerTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, workoutViewModel = workoutViewModel)
            }
        }
    }
}
