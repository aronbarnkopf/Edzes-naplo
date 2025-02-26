package com.workouttracker.db.model
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long // Unix timestamp
)

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM Workout")
    fun getAll(): List<Workout>

    @Query("SELECT * FROM Workout WHERE id = :id")
    fun getById(id: Int): Workout?

    @Insert
    fun insert(workout: Workout): Long

    @Update
    fun update(workout: Workout)

    @Delete
    fun delete(workout: Workout)
}