package com.workouttracker.db.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class TimerPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Pl. "HIIT 30-10"
    val repeatCount: Int // Hányszor ismétlődjön a teljes szekvencia (pl. 8)
)

@Dao
interface TimerPresetDao {
    @Query("SELECT * FROM TimerPreset")
    fun getAll(): List<TimerPreset>

    @Query("SELECT * FROM TimerPreset WHERE id = :id")
    fun getById(id: Int): TimerPreset?

    @Insert
    fun insert(timerPreset: TimerPreset): Long

    @Update
    fun update(timerPreset: TimerPreset)

    @Delete
    fun delete(timerPreset: TimerPreset)
}
