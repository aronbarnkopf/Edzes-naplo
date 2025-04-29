package com.workouttracker.db.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface WorkoutDao {
    // Edzés beszúrása
    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    // Gyakorlat beszúrása
    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    // WorkoutExercise beszúrása (kapcsolat edzés és gyakorlat között)
    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long

    // Sett beszúrása (egy adott WorkoutExercise-hez)
    @Insert
    suspend fun insertSett(sett: Sett): Long

    @Transaction
    @Query("SELECT * FROM Workout WHERE isSaved = 0")
    suspend fun getAllWorkouts(): List<Workout>

    @Transaction
    @Query("SELECT * FROM Workout WHERE isSaved = 1")
    suspend fun getAllSavedWorkouts(): List<Workout>

    @Transaction
    @Query("SELECT * FROM Exercise")
    suspend fun getAllExercises(): List<Exercise>

    @Transaction
    @Query("SELECT * FROM Workout WHERE isSaved = 0")
    suspend fun getAllWorkoutWithExercises(): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM Workout WHERE isSaved = 1")
    suspend fun getAllSavedWorkoutWithExercises(): List<WorkoutWithExercises>

    @Query("DELETE FROM Workout")
    suspend fun deleteAllWorkouts()

    @Query("DELETE FROM Exercise")
    suspend fun deleteAllExercises()

    @Query("DELETE FROM WorkoutExercise")
    suspend fun deleteAllWorkoutExercises()

    @Query("DELETE FROM Sett")
    suspend fun deleteAllSetts()

    suspend fun deleteAll(){
        deleteAllSetts()
        deleteAllWorkoutExercises()
        deleteAllExercises()
        deleteAllWorkouts()
    }
    @Transaction
    @Query("SELECT * FROM Workout WHERE id IN (SELECT workoutId FROM WorkoutExercise WHERE exerciseId = :exerciseId)")
    suspend fun getWorkoutWithExercise(exerciseId: Int): List<WorkoutWithExercises>
}
