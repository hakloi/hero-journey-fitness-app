package com.example.fitnessapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseType {
    CARDIO,
    LOWER_BODY,
    UPPER_BODY,
    CORE,
    BALANCE,
    MOBILITY
}

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val count: Int,
    val exerciseType: ExerciseType = ExerciseType.LOWER_BODY,
    val timestamp: Long = System.currentTimeMillis()
)
