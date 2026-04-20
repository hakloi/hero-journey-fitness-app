package com.example.fitnessapp.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.entities.ExerciseType
import com.example.fitnessapp.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetersWarmupViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    fun saveSession(count: Int) {
        if (count > 0) {
            viewModelScope.launch {
                repository.addExercise(count, ExerciseType.CARDIO)
            }
        }
    }
}
