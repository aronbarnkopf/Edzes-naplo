package com.workouttracker.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class TimerPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Pl. "HIIT 30-10"
    val repeatCount: Int // Hányszor ismétlődjön a teljes szekvencia (pl. 8)
)
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
data class TimerPresetWithIntervals(
    @Embedded val timerPreset: TimerPreset,
    @Relation(
        parentColumn = "id",
        entityColumn = "timerPresetId"
    )
    val intervals: List<TimerInterval>
)
