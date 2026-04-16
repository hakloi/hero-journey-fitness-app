package com.example.fitnessapp.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(): ViewModel() {
    private val _isMusicPlaying = MutableStateFlow(false) // доступ ТОЛЬКО во вью модел
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow() // доступ вью для чтения!

    private var musicServiceCallback: ((Boolean) -> Unit)? = null

    fun setMusicServiceCallback(callback: (Boolean) -> Unit) {
        musicServiceCallback = callback
    }

    fun toggleMusic() {
        val newState = !_isMusicPlaying.value
        _isMusicPlaying.value = newState
        musicServiceCallback?.invoke(newState)  // говорим сервису: включи/выключи
    }

    fun updateMusicState(isPlaying: Boolean) {
        if (_isMusicPlaying.value != isPlaying) {
            _isMusicPlaying.value = isPlaying
        }
    }
}