package com.mjdev.musicplayer.presentation.themeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeScreenViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _states = MutableStateFlow(ThemeStates())
    val states = _states.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.readImageTheme().collectLatest {
                _states.value = states.value.copy(imageTheme = ImageTheme.valueOf(it) ?: ImageTheme.None)
            }
        }

        viewModelScope.launch {
            themeRepository.readGradient().collectLatest {
                _states.value = states.value.copy(gradient = Gradient.valueOf(it) ?: Gradient.None)
            }
        }
    }

    fun events(events: ThemeEvents){
        when(events){
            is ThemeEvents.GradientChange -> {
                viewModelScope.launch {
                    themeRepository.insertGradient(events.gradient)
                    themeRepository.insertImageTheme(ImageTheme.None)

                }
//                _states.value = states.value.copy(gradient = events.gradient)
            }
            is ThemeEvents.ImageThemeChange -> {
                viewModelScope.launch {
                    themeRepository.insertImageTheme(events.imageTheme)
                    themeRepository.insertGradient(Gradient.None)
                }
//                _states.value = states.value.copy(imageTheme = events.imageTheme)
            }

            is ThemeEvents.IsGradientChange -> {
                _states.value = states.value.copy(isGradient = events.isGradient)
            }

        }
    }
}