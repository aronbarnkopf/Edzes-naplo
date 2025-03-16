package com.workouttracker.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import java.util.Date

@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Date,
    val isSaved: Boolean
)
@Entity(
    foreignKeys = [
        ForeignKey(entity = Workout::class, parentColumns = ["id"], childColumns = ["workoutId"]),
        ForeignKey(entity = Exercise::class, parentColumns = ["id"], childColumns = ["exerciseId"])
    ]
)
data class WorkoutExercise(//Connects the Workout and the Exercise
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int, // Melyik edzéshez tartozik
    val exerciseId: Int // Melyik gyakorlatot végezték
)
@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Pl. "Bench Press"
    val type: String, // Pl. "Strength", "Cardio"
)
@Entity(
    foreignKeys = [
        ForeignKey(entity = WorkoutExercise::class, parentColumns = ["id"], childColumns = ["workoutExerciseId"])
    ]
)
data class Sett(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutExerciseId: Int, // Melyik WorkoutExercise-hez tartozik
    val weight: Double, // Súly (kg)
    val reps: Int // Ismétlések száma
)

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = WorkoutExercise::class
    )
    val workoutExercises: List<ExerciseWithSetts>
)
data class ExerciseWithSetts(
    @Embedded val workoutExercise: WorkoutExercise,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: Exercise,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val setts: List<Sett>
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
