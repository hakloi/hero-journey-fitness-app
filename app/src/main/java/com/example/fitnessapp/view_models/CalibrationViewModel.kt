package com.example.fitnessapp.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.repository.FitnessRepository
import com.example.fitnessapp.sensors.Sensor
import kotlinx.coroutines.launch
import kotlin.math.*

class CalibrationViewModel(
    private val sensor: Sensor,
    private val repository: FitnessRepository
) : ViewModel() {

    // Текущий угол наклона
    val currentAngle = mutableStateOf(0f)

    // Запомненный угол (калибровка)
    val calibrationAngle = mutableStateOf<Float?>(null)

    val isCalibrated = mutableStateOf(false)
    val isCalibrating = mutableStateOf(false)
    val countdown = mutableStateOf(0)

    init {
        loadSavedCalibration()
        sensor.startListening()

        viewModelScope.launch {
            sensor.sharedFlow.collect { values ->
                val x = values.getOrElse(0) { 0f }
                val y = values.getOrElse(1) { 0f }
                val z = values.getOrElse(2) { 0f }

                // Вычисляем угол наклона
                val angle = calculateTiltAngle(x, y, z)
                currentAngle.value = angle

            }
        }
    }

    private fun calculateTiltAngle(x: Float, y: Float, z: Float): Float {
        val angleRad = atan2(
            sqrt((x * x + y * y).toDouble()),
            z.toDouble()
        )
        return Math.toDegrees(angleRad).toFloat()
    }

    private fun loadSavedCalibration() {
        viewModelScope.launch {
            repository.getCalibration().collect { calibration ->
                if (calibration != null) {
                    calibrationAngle.value = calibration.standAngle
                    isCalibrated.value = true
                }
            }
        }
    }

    fun startCalibration() {
        isCalibrating.value = true
        countdown.value = 3
        startCountdown()
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (countdown.value > 0) {
                kotlinx.coroutines.delay(1000)
                countdown.value--
            }
            if (countdown.value == 0 && isCalibrating.value) {
                finishCalibration()
            }
        }
    }

    private fun finishCalibration() {
        val angle = currentAngle.value
        calibrationAngle.value = angle
        isCalibrated.value = true
        isCalibrating.value = false

        viewModelScope.launch {
            repository.saveCalibration(standAngle = angle)
        }
    }

    fun resetCalibration() {
        calibrationAngle.value = null
        isCalibrated.value = false
        isCalibrating.value = false
        countdown.value = 0

        viewModelScope.launch {
            repository.deleteCalibration()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensor.stopListening()
    }
}