package com.example.fitnessapp.DI

import android.content.Context
import com.example.fitnessapp.DAO.CalibrationDao
import com.example.fitnessapp.DAO.ExerciseDao
import com.example.fitnessapp.database.AppDatabase
import com.example.fitnessapp.repository.FitnessRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCalibrationDao(database: AppDatabase): CalibrationDao {
        return database.calibrationDao()
    }

    @Provides
    @Singleton
    fun provideExerciseDao(database: AppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideFitnessRepository(
        calibrationDao: CalibrationDao,
        exerciseDao: ExerciseDao
    ): FitnessRepository {
        return FitnessRepository(calibrationDao, exerciseDao)
    }
}