package com.example.fitnessapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calibration")
data class CalibrationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val standAngle: Float
)