package com.workouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workouttracker.db.WorkoutRepository
import com.workouttracker.db.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    private val _availableExercises = MutableStateFlow<List<Exercise>>(emptyList())
    private val _savedWorkouts = MutableStateFlow<List<WorkoutWithExercises>>(emptyList())
    private val _temporaryWorkoutWithExercises = MutableStateFlow<WorkoutWithExercises?>(null) // New property to store temp workout

    val availableExercises: StateFlow<List<Exercise>> = _availableExercises
    val savedWorkouts: StateFlow<List<WorkoutWithExercises>> = _savedWorkouts
    val temporaryWorkoutWithExercises: StateFlow<WorkoutWithExercises?> = _temporaryWorkoutWithExercises

    init {
        getAllExercises()
        getAllSavedWorkouts()
    }

    // Workout hozzáadása
    fun addWorkout(workout: Workout, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertWorkout(workout)
            onSuccess(id)
        }
    }

    // WorkoutExercise hozzáadása
    fun addWorkoutExercise(workoutExercise: WorkoutExercise, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertWorkoutExercise(workoutExercise)
            onSuccess(id)
        }
    }

    // Exercise hozzáadása
    fun addExercise(exercise: Exercise, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertExercise(exercise)
            getAllExercises()
            onSuccess(id)
        }
    }

    // Sett hozzáadása
    fun addSett(sett: Sett, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertSett(sett)
            onSuccess(id)
        }
    }

    // WorkoutWithExercises mentése
    fun addWorkoutWithExercises(workoutWithExercises: WorkoutWithExercises, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertWorkoutWithExercises(workoutWithExercises)
            getAllSavedWorkouts()
            onSuccess()
        }
    }

    // Összes Workout lekérése
    fun getAllWorkout(onResult: (List<WorkoutWithExercises>) -> Unit) {
        viewModelScope.launch {
            val workouts = repository.getAllWorkoutWithExercises()
            onResult(workouts)
        }
    }


    // Összes Exercise lekérése
    fun getAllExercises(onResult: (List<Exercise>) -> Unit) {
        viewModelScope.launch {
            val exercises = repository.getAllExercises()
            onResult(exercises)
        }
    }

    private fun getAllExercises() {
        viewModelScope.launch {
            _availableExercises.value = repository.getAllExercises()
        }
    }

    private fun getAllSavedWorkouts() {
        viewModelScope.launch {
            _savedWorkouts.value = repository.getAllSavedWorkoutWithExercises()
        }
    }

    fun getAllSavedWorkout(onResult: (List<WorkoutWithExercises>) -> Unit) {
        viewModelScope.launch {
            val savedWorkouts = repository.getAllSavedWorkoutWithExercises()
            onResult(savedWorkouts)
        }
    }

    // Method to set the temporary workout
    fun setTemporaryWorkout(workoutWithExercises: WorkoutWithExercises) {
        _temporaryWorkoutWithExercises.value = workoutWithExercises
    }

    // Method to clear the temporary workout
    fun clearTemporaryWorkout() {
        _temporaryWorkoutWithExercises.value = null
    }

    fun deleteAll(){
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
