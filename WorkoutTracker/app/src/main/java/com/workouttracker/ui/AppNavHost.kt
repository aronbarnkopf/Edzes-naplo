package com.workouttracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.workouttracker.ui.screen.*
import com.workouttracker.viewmodel.TimerViewModel
import com.workouttracker.viewmodel.WorkoutViewModel


@Composable
fun AppNavHost(navController: NavHostController, workoutViewModel: WorkoutViewModel, timerViewModel: TimerViewModel) {
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
        composable("timer_setup") {
            TimerSetupScreen(onStart = {})
        }
        composable("workout_detail") {
            WorkoutDetailScreen(viewModel = workoutViewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("timer_list") {
            TimerListScreen(viewModel = timerViewModel, onNavigate = { route -> navController.navigate(route) })
        }
        composable("timer_run") {
            val context = LocalContext.current
            TimerRunScreen(viewModel = timerViewModel, context = context)
        }
        composable("timer")
        {
            val context = LocalContext.current
            TimerScreen(context)
        }
    }
}
