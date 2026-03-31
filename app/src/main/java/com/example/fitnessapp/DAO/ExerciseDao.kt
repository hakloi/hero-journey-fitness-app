package com.example.fitnessapp.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnessapp.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * from exercise ORDER BY timestamp DESC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT SUM(count) FROM exercise")
    fun getTotalSquats(): Flow<Int?>

    @Insert
    suspend fun addExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercise")
    suspend fun deleteAllExercises()
}