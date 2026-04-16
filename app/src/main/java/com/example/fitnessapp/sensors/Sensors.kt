package com.example.fitnessapp.sensors

import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.Sensor as InternalSensor
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class SensorInfo(val name: String, val stringType: String)

class Sensor(
    private val internalSensor: InternalSensor,
    private val sensorManager: SensorManager,
    private val coroutineScope: CoroutineScope
) {

    private val _sharedFlow = MutableSharedFlow<List<Float>>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    private val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: InternalSensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            coroutineScope.launch(Dispatchers.Default) {
                _sharedFlow.emit(event.values.toList())
            }
        }
    }

    fun startListening() {
        sensorManager.registerListener(listener, internalSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stopListening() {
        sensorManager.unregisterListener(listener)
    }
}

class Sensors(private val sensorManager: SensorManager) {

    fun getSensor(scope: CoroutineScope): Sensor? {
        val s = sensorManager.getDefaultSensor(InternalSensor.TYPE_GRAVITY)
        return s?.let { Sensor(it, sensorManager, scope) }
    }

    fun getAllSensors(): List<SensorInfo> {
        return sensorManager.getSensorList(InternalSensor.TYPE_ALL).map {
            SensorInfo(it.name, it.stringType)
        }
    }
}