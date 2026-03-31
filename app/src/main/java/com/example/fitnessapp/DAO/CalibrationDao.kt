package com.example.fitnessapp.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.entities.CalibrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalibrationDao {
    @Query("SELECT * FROM calibration WHERE id = 1")
    fun getCalibration(): Flow<CalibrationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCalibration(calibration: CalibrationEntity)

    @Query("DELETE FROM calibration")
    suspend fun deleteCalibration()
}