package com.workouttracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.workouttracker.db.model.*
//Usage: https://developer.android.com/training/data-storage/room
@Database(entities = [Workout::class, Exercise::class, WorkoutExercise::class, Set::class, TimerPreset::class, TimerInterval::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun setDao(): SetDao
    abstract fun timerPresetDao(): TimerPresetDao
    abstract fun imerIntervalDao(): TimerIntervalDao
}