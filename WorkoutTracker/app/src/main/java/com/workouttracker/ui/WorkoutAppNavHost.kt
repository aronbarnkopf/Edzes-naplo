package com.workouttracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.workouttracker.ui.screen.*
import com.workouttracker.viewmodel.WorkoutViewModel


@Composable
fun WorkoutAppNavHost(navController: NavHostController, viewModel: WorkoutViewModel) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("workout_log") {
            WorkoutLogScreen(viewModel = viewModel, onNavigate = { route -> navController.navigate(route) })
        }
        composable("new_workout") {
            NewWorkoutScreen(viewModel = viewModel, onNavigate = { navController.popBackStack() })
        }
        composable("timer_setup") {
            TimerSetupScreen(onStart = {})
        }
        composable("workout_detail") {
            WorkoutDetailScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
        }
    }
}
