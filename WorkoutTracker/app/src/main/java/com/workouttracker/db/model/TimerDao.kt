package com.workouttracker.db.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TimerDao {
    @Insert
    suspend fun insertTimerPreset(timerPreset: TimerPreset): Long

    @Insert
    suspend fun insertTimerInterval(timerInterval: TimerInterval): Long


    @Transaction
    @Query("SELECT * FROM TimerPreset WHERE id = :presetId")
    suspend fun getTimerPresetWithIntervals(presetId: Int): TimerPresetWithIntervals

    @Transaction
    @Query("SELECT * FROM TimerPreset")
    suspend fun getAllTimerPresetsWithIntervals(): List<TimerPresetWithIntervals>

    @Delete
    suspend fun deleteInterval(timerInterval: TimerInterval)

    @Delete
    suspend fun deleteTimerPreset(timerPreset: TimerPreset)

    @Delete
    suspend fun deleteTimerPresetWithIntervals(timerPreset: TimerPreset)

    @Insert
    suspend fun insertIntervals(updatedIntervals: List<TimerInterval>)

    @Transaction
    @Query("SELECT * FROM TimerPreset")
    suspend fun getAllTimerPresets(): List<TimerPreset>

}
