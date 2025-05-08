package com.workouttracker.db

import com.workouttracker.db.model.*

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout)
    }

    suspend fun insertExercise(exercise: Exercise): Long {
        return workoutDao.insertExercise(exercise)
    }

    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long {
        return workoutDao.insertWorkoutExercise(workoutExercise)
    }

    suspend fun insertSett(sett: Sett): Long {
        return workoutDao.insertSett(sett)
    }

    suspend fun insertWorkoutWithExercises(workoutWithExercises: WorkoutWithExercises) {
        workoutDao.apply {
            // Insert the workout and get the workoutId
            val workoutId = insertWorkout(workoutWithExercises.workout)

            workoutWithExercises.workoutExercises.forEach { exerciseWithSetts ->
                // Insert each WorkoutExercise with the correct workoutId
                val workoutExercise = exerciseWithSetts.workoutExercise.copy(workoutId = workoutId.toInt())
                val workoutExerciseId = insertWorkoutExercise(workoutExercise)

                // Insert all Setts related to the WorkoutExercise
                exerciseWithSetts.setts.forEach { sett ->
                    val settCopy = sett.copy(workoutExerciseId = workoutExerciseId.toInt())
                    insertSett(settCopy)
                }
            }
        }
    }

    suspend fun getAllWorkouts(): List<Workout> {
        return workoutDao.getAllWorkouts()
    }

    suspend fun getAllSavedWorkouts(): List<Workout> {
        return workoutDao.getAllSavedWorkouts()
    }

    suspend fun getAllExercises(): List<Exercise> {
        return workoutDao.getAllExercises()
    }

    suspend fun getAllWorkoutWithExercises(): List<WorkoutWithExercises> {
        return workoutDao.getAllWorkoutWithExercises()
    }

    suspend fun getAllSavedWorkoutWithExercises(): List<WorkoutWithExercises> {
        return workoutDao.getAllSavedWorkoutWithExercises()
    }

    suspend fun deleteAll() {
        workoutDao.deleteAll()
    }

    suspend fun getWorkoutWithExercise(exerciseId: Int): List<WorkoutWithExercises> {
        return workoutDao.getWorkoutWithExercise(exerciseId)
    }

    suspend fun getLastSetts(id: Int): List<Sett> {
        val workouts = workoutDao.getWorkoutWithExercise(id).toMutableList()
        if (workouts.isEmpty()) return emptyList()

        workouts.sortByDescending { it.workout.date }

        val firstWorkoutExercise = workouts[0].workoutExercises.find { it.exercise.id == id }
        return firstWorkoutExercise?.setts ?: emptyList()
    }

    suspend fun removeWorkout(id: Int) {
        workoutDao.removeWorkoutExerises(id)
        workoutDao.removeWorkout(id)
    }

}

