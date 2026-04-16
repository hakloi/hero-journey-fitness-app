package com.example.fitnessapp.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.repository.FitnessRepository
import com.example.fitnessapp.sensors.Sensor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.*

class SquatCounterViewModel(
    private val sensor: Sensor,
    private val repository: FitnessRepository
) : ViewModel() {

    val totalCount = mutableStateOf(0)
    val currentSessionCount = mutableStateOf(0)
    val currentAngle = mutableStateOf(0f)

    private var calibrationAngle = 0f
    private var isCalibrated = false
    private val squatThreshold = 15f

    private enum class SquatState { UP, DOWN }
    private var currentState = SquatState.UP

    private var collectingJob: Job? = null

    init {
        loadCalibration()

        sensor.startListening()

        collectingJob = viewModelScope.launch {
            sensor.sharedFlow.collect { values ->
                val x = values.getOrElse(0) { 0f }
                val y = values.getOrElse(1) { 0f }
                val z = values.getOrElse(2) { 0f }

                val angle = calculateTiltAngle(x, y, z)
                currentAngle.value = angle

                if (isCalibrated) {
                    processSquat(angle)
                }
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

    private fun loadCalibration() {
        viewModelScope.launch {
            repository.getCalibration().collect { calibration ->
                if (calibration != null) {
                    calibrationAngle = calibration.standAngle
                    isCalibrated = true
                    return@collect
                }
            }
        }
    }

    private fun processSquat(angle: Float) {
        val deltaAngle = angle - calibrationAngle

        when (currentState) {
            SquatState.UP -> {
                if (deltaAngle > squatThreshold) {
                    currentState = SquatState.DOWN
                }
            }
            SquatState.DOWN -> {
                if (deltaAngle < squatThreshold / 2) {
                    currentState = SquatState.UP
                    totalCount.value++
                    currentSessionCount.value++
                }
            }
        }
    }

    fun saveCurrentSession() {
        if (currentSessionCount.value > 0) {
            viewModelScope.launch {
                repository.addExercise(currentSessionCount.value)
                currentSessionCount.value = 0
            }
        }
    }

    fun resetCount() {
        currentSessionCount.value = 0
    }

    fun hasCalibration(): Boolean = isCalibrated

    fun getCalibrationAngle(): Float = calibrationAngle

    override fun onCleared() {
        super.onCleared()
        collectingJob?.cancel()
        sensor.stopListening()
    }
}