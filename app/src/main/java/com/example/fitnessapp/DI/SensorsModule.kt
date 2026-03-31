package com.example.fitnessapp.DI

import android.app.Application
import android.hardware.SensorManager
import com.example.fitnessapp.sensors.Sensors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorsModule {

    @Provides
    @Singleton
    fun provideSensorManager(app: Application): SensorManager {
        return app.getSystemService(SensorManager::class.java)
    }

    @Provides
    @Singleton
    fun provideSensors(sensorManager: SensorManager): Sensors {
        return Sensors(sensorManager)
    }
}