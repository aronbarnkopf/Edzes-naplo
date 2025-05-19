package com.workouttracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.workouttracker.ui.screen.*
import com.workouttracker.viewmodel.WorkoutViewModel


@Composable
fun AppNavHost(navController: NavHostController, workoutViewModel: WorkoutViewModel) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("workout_log") {
            WorkoutLogScreen(viewModel = workoutViewModel, onNavigate = { route -> navController.navigate(route) })
        }
        composable("new_workout") {
            NewWorkoutScreen(viewModel = workoutViewModel, onNavigate = { navController.popBackStack() })
        }
        composable("workout_detail") {
            WorkoutDetailScreen(viewModel = workoutViewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("timer")
        {
            val context = LocalContext.current
            TimerScreen(context)
        }
        composable("stats") {
            StatScreen(viewModel = workoutViewModel)
        }
    }
}
