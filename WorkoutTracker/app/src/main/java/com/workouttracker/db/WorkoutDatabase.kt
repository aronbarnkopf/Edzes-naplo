package com.workouttracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.workouttracker.db.model.*
//Usage: https://developer.android.com/training/data-storage/room
@Database(entities = [Workout::class, WorkoutExercise::class,
    Exercise::class, Sett::class,
    TimerPreset::class, TimerInterval::class],
    version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
