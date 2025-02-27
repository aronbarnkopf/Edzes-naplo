package com.workouttracker.db.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Pl. "Bench Press"
    val type: String // Pl. "Strength", "Cardio"
)

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    fun getAll(): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE id = :id")
    fun getById(id: Int): Exercise?

    @Insert
    fun insert(exercise: Exercise): Long

    @Update
    fun update(exercise: Exercise)

    @Delete
    fun delete(exercise: Exercise)
}