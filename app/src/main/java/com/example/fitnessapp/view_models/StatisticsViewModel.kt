package com.example.fitnessapp.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.entities.ExerciseEntity
import com.example.fitnessapp.entities.ExerciseType
import com.example.fitnessapp.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> = _exercises.asStateFlow()

    private val _totalSquats = MutableStateFlow(0)
    val totalSquats: StateFlow<Int> = _totalSquats.asStateFlow()

    // Суммы по категориям для круговой диаграммы
    private val _categoryTotals = MutableStateFlow<Map<ExerciseType, Int>>(emptyMap())
    val categoryTotals: StateFlow<Map<ExerciseType, Int>> = _categoryTotals.asStateFlow()

    val streak: StateFlow<Int> get() = _streak
    private val _streak = MutableStateFlow(0)

    init {
        loadExercises()
        loadTotalSquats()
        loadCategoryTotals()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            repository.getAllExercises().collect {
                _exercises.value = it
                _streak.value = calculateStreak(it)
            }
        }
    }

    private fun loadTotalSquats() {
        viewModelScope.launch {
            repository.getTotalSquats().collect { _totalSquats.value = it ?: 0 }
        }
    }

    private fun loadCategoryTotals() {
        ExerciseType.entries.forEach { type ->
            viewModelScope.launch {
                repository.getTotalByType(type).collect { total ->
                    _categoryTotals.value = _categoryTotals.value + (type to (total ?: 0))
                }
            }
        }
    }

    private fun calculateStreak(exercises: List<ExerciseEntity>): Int {
        if (exercises.isEmpty()) return 0
        val fmt = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        val days = exercises.map { fmt.format(java.util.Date(it.timestamp)) }.toSortedSet().toList().reversed()
        val today = fmt.format(java.util.Date())
        if (days.first() != today) return 0
        var streak = 1
        val cal = java.util.Calendar.getInstance()
        for (i in 1 until days.size) {
            cal.time = java.util.Date()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
            if (fmt.format(cal.time) == days[i]) streak++ else break
        }
        return streak
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch { repository.deleteExercise(exercise) }
    }

    fun deleteAllExercises() {
        viewModelScope.launch { repository.deleteAllExercises() }
    }
}
