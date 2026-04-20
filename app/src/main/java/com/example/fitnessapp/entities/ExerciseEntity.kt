package com.example.fitnessapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseType {
    CARDIO,       // Peter's Warmup
    LOWER_BODY,   // Thunder Kick
    UPPER_BODY,   // Wall Crawler
    CORE,         // Leap of Faith
    MOBILITY      // Gwen's Rest
}

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val count: Int,
    val exerciseType: ExerciseType = ExerciseType.LOWER_BODY,
    val timestamp: Long = System.currentTimeMillis()
)
