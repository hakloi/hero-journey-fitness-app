package com.example.fitnessapp.repository

import com.example.fitnessapp.DAO.CalibrationDao
import com.example.fitnessapp.DAO.ExerciseDao
import com.example.fitnessapp.entities.CalibrationEntity
import com.example.fitnessapp.entities.ExerciseEntity
import com.example.fitnessapp.entities.ExerciseType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FitnessRepository @Inject constructor(
    private val calibrationDao: CalibrationDao,
    private val exerciseDao: ExerciseDao
) {

    // ========== Calibration ==========

    fun getCalibration(): Flow<CalibrationEntity?> {
        return calibrationDao.getCalibration()
    }

    suspend fun saveCalibration(standAngle: Float) {
        val calibration = CalibrationEntity(
            id = 1,
            standAngle = standAngle
        )
        calibrationDao.saveCalibration(calibration)
    }

    suspend fun deleteCalibration() {
        calibrationDao.deleteCalibration()
    }

    // ========== Exercise ==========

    fun getAllExercises(): Flow<List<ExerciseEntity>> {
        return exerciseDao.getAllExercises()
    }

    suspend fun addExercise(count: Int, exerciseType: ExerciseType) {
        val exercise = ExerciseEntity(
            count = count,
            exerciseType = exerciseType,
            timestamp = System.currentTimeMillis()
        )
        exerciseDao.addExercise(exercise)
    }

    fun getTotalSquats(): Flow<Int?> {
        return exerciseDao.getTotalSquats()
    }

    fun getTotalByType(type: ExerciseType): Flow<Int?> {
        return exerciseDao.getTotalByType(type)
    }

    suspend fun deleteExercise(exercise: ExerciseEntity) {
        exerciseDao.deleteExercise(exercise)
    }

    suspend fun deleteAllExercises() {
        exerciseDao.deleteAllExercises()
    }
}
