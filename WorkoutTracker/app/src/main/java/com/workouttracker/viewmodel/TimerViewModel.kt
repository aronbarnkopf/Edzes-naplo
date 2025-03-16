package com.workouttracker.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workouttracker.R
import com.workouttracker.db.TimerRepository
import com.workouttracker.db.model.Exercise
import com.workouttracker.db.model.TimerInterval
import com.workouttracker.db.model.TimerPreset
import com.workouttracker.db.model.TimerPresetWithIntervals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {
    private val _timerPresets = MutableStateFlow<List<TimerPreset>>(emptyList())
    val timerPresets: StateFlow<List<TimerPreset>> = _timerPresets
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    init {
        getAllTimerPresets()
    }
    private fun getAllTimerPresets(){
        viewModelScope.launch {
            _timerPresets.value = repository.getAllTimerPresets()
        }
    }

    fun setTimerState(id: Int, index: Int, isRunning: Boolean){
        viewModelScope.launch {
            val timerPresetWithIntervals = repository.getTimerPresetWithIntervals(id)
            _timerState.value = TimerState(
                presetId = timerPresetWithIntervals.timerPreset.id,
                currentIntervalType = timerPresetWithIntervals.intervals[index].type,
                currentIntervalIndex = 0,
                remainingTime = timerPresetWithIntervals.intervals[index].duration,
                intervalCount = timerPresetWithIntervals.intervals.size,
                repeatCount = timerPresetWithIntervals.timerPreset.repeatCount,
                isRunning = isRunning
            )
        }
    }

    fun addTimerPreset(name: String, repeatCount: Int, intervals: List<TimerInterval>) {
        viewModelScope.launch {
            repository.insertFullTimerPreset(TimerPreset(name = name, repeatCount = repeatCount), intervals)
            getAllTimerPresets()
        }
    }

    fun deleteTimerPreset(timerPreset: TimerPreset) {
        viewModelScope.launch {
            repository.deleteTimerPreset(timerPreset)
            getAllTimerPresets()
        }
    }

    fun decrementTimer() {
        _timerState.value = _timerState.value.copy(remainingTime = _timerState.value.remainingTime-1)
    }

    fun nextInterval() {
        val nextIndex =
            if (_timerState.value.currentIntervalIndex < _timerState.value.intervalCount - 1)
                _timerState.value.currentIntervalIndex + 1
            else
                0;
        setTimerState(_timerState.value.presetId, nextIndex, true)
    }

    fun toggleTimer() {
        _timerState.value = _timerState.value.copy(isRunning = !_timerState.value.isRunning)
    }

    fun resetTimer() {
        setTimerState(_timerState.value.presetId, 0, false)
    }
}

data class TimerState(
    var presetId: Int = 0,
    var isRunning: Boolean = false,
    var remainingTime: Int = 0, // másodpercben
    var currentIntervalIndex: Int = 0,
    val intervalCount: Int = 0,
    var currentIntervalType: String = "work",
    var repeatCount: Int = 1
)
