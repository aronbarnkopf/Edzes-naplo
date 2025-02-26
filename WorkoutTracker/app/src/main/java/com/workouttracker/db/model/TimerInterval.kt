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
        ForeignKey(entity = TimerPreset::class, parentColumns = ["id"], childColumns = ["timerPresetId"])
    ]
)
data class TimerInterval(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timerPresetId: Int, // Melyik időzítőhöz tartozik
    val duration: Int, // Időtartam másodpercben (pl. 30 vagy 10)
    val type: String // "work", "rest", "warmup", stb.
)

@Dao
interface TimerIntervalDao {
    @Query("SELECT * FROM TimerInterval WHERE timerPresetId = :timerPresetId")
    fun getByTimerPresetId(timerPresetId: Int): List<TimerInterval>

    @Insert
    fun insert(timerInterval: TimerInterval): Long

    @Update
    fun update(timeInterval: TimerInterval)

    @Delete
    fun delete(timerInterval: TimerInterval)
}