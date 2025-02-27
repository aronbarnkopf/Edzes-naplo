package com.workouttracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.workouttracker.ui.screen.*


@Composable
fun WorkoutAppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("workout_log") {
            WorkoutLogScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("new_workout") {
            NewWorkoutScreen(onSave = {})
        }
        composable("timer_setup") {
            TimerSetupScreen(onStart = {})
        }
    }
}