package com.example.fitnessapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val count: Int,
    val timestamp: Long = System.currentTimeMillis()
)