package com.example.fitnessapp.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.entities.ExerciseEntity
import com.example.fitnessapp.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticsViewModel(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> = _exercises.asStateFlow()

    private val _totalSquats = MutableStateFlow(0)
    val totalSquats: StateFlow<Int> = _totalSquats.asStateFlow()

    init {
        loadExercises()
        loadTotalSquats()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            repository.getAllExercises().collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }
    }

    private fun loadTotalSquats() {
        viewModelScope.launch {
            repository.getTotalSquats().collect { total ->
                _totalSquats.value = total ?: 0
            }
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun deleteAllExercises() {
        viewModelScope.launch {
            repository.deleteAllExercises()
        }
    }
}