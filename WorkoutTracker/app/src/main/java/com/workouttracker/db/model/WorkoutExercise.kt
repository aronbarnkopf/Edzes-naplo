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
        ForeignKey(entity = Workout::class, parentColumns = ["id"], childColumns = ["workoutId"]),
        ForeignKey(entity = Exercise::class, parentColumns = ["id"], childColumns = ["exerciseId"])
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int, // Melyik edzéshez tartozik
    val exerciseId: Int // Melyik gyakorlatot végezték
)

@Dao
interface WorkoutExerciseDao {
    @Query("SELECT * FROM WorkoutExercise WHERE workoutId = :workoutId")
    fun getByWorkoutId(workoutId: Int): List<WorkoutExercise>

    @Insert
    fun insert(workoutExercise: WorkoutExercise): Long

    @Update
    fun update(workoutExercise: WorkoutExercise)

    @Delete
    fun delete(workoutExercise: WorkoutExercise)
}