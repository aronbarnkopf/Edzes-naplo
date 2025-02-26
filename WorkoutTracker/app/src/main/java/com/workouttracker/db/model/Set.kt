package com.workouttracker.db.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(
    foreignKeys = [
        ForeignKey(entity = WorkoutExercise::class, parentColumns = ["id"], childColumns = ["workoutExerciseId"])
    ]
)
data class Set(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutExerciseId: Int, // Melyik WorkoutExercise-hez tartozik
    val weight: Float?, // Súly (kg)
    val reps: Int // Ismétlések száma
)

@Dao
interface SetDao {
    @Query("SELECT * FROM Set WHERE workoutExerciseId = :workoutExerciseId")
    fun getByWorkoutExerciseId(workoutExerciseId: Int): List<Set>

    @Insert
    fun insert(set: Set): Long

    @Update
    fun update(set: Set)

    @Delete
    fun delete(set: Set)
}