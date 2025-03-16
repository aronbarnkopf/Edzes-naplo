package com.workouttracker.db

import androidx.room.Transaction
import com.workouttracker.db.model.TimerDao
import com.workouttracker.db.model.TimerInterval
import com.workouttracker.db.model.TimerPreset
import com.workouttracker.db.model.TimerPresetWithIntervals

class TimerRepository(private val timerDao: TimerDao) {

    // Lekéri az összes időzítő presetet és azok intervallumait
    suspend fun getAllTimerPresetsWithIntervals(): List<TimerPresetWithIntervals> {
        return timerDao.getAllTimerPresetsWithIntervals()
    }

    // Egy adott preset és annak intervallumainak lekérdezése
    suspend fun getTimerPresetWithIntervals(presetId: Int): TimerPresetWithIntervals {
        return timerDao.getTimerPresetWithIntervals(presetId)
    }

    // Új időzítő preset mentése (és visszaadja az ID-t)
    suspend fun insertTimerPreset(timerPreset: TimerPreset): Long {
        return timerDao.insertTimerPreset(timerPreset)
    }

    // Új intervallumok mentése
    suspend fun insertIntervals(intervals: List<TimerInterval>) {
        timerDao.insertIntervals(intervals)
    }

    // Egy teljes időzítő preset és annak intervallumainak mentése tranzakcióként
    @Transaction
    suspend fun insertFullTimerPreset(timerPreset: TimerPreset, intervals: List<TimerInterval>) {
        val presetId = timerDao.insertTimerPreset(timerPreset).toInt()
        val updatedIntervals = intervals.map { it.copy(timerPresetId = presetId) }
        timerDao.insertIntervals(updatedIntervals)
    }

    // Egy preset törlése (és automatikusan törli a kapcsolódó intervallumokat)
    suspend fun deleteTimerPreset(timerPreset: TimerPreset) {
        timerDao.deleteTimerPreset(timerPreset)
    }

    // Egy intervallum törlése
    suspend fun deleteInterval(timerInterval: TimerInterval) {
        timerDao.deleteInterval(timerInterval)
    }

    suspend fun deleteTimerPresetWithIntervals(timerPreset: TimerPreset) {
        timerDao.deleteTimerPresetWithIntervals(timerPreset)
    }

    suspend fun getAllTimerPresets(): List<TimerPreset> {
        return timerDao.getAllTimerPresets()
    }
}
