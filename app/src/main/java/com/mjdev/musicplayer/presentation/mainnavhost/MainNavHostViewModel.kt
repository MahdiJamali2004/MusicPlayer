@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mjdev.musicplayer.presentation.mainnavhost

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainNavHostViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _states = MutableStateFlow(MainNavHostState())
    val states = _states.asStateFlow()


    init {

        mainRepository.getOption()
            .onEach { _states.value = states.value.copy(option = it ?: Option()) }.launchIn(viewModelScope)

        viewModelScope.launch {
            mainRepository.getAllMusicSortedBy().collectLatest {
                _states.value = states.value.copy(musics = it)
            }

        }
        viewModelScope.launch {
            themeRepository.readImageTheme().collectLatest {
                Log.v("imageWorked","$it")
                _states.value = states.value.copy(imageTheme = ImageTheme.valueOf(it) ?: ImageTheme.None)
            }
        }

        viewModelScope.launch {
            themeRepository.readGradient().collectLatest {
                _states.value = states.value.copy(gradient = Gradient.valueOf(it) ?: Gradient.None)
            }
        }

    }

    fun events(event: MainNavHostEvents) {
        when (event) {
            is MainNavHostEvents.GridNumChanged -> {
                _states.value =
                    states.value.copy(option = states.value.option.copy(gridNum = event.gridNum))
                updateOption(states.value.option)
            }


            is MainNavHostEvents.ImgCornerShapeChanged -> {
                _states.value =
                    states.value.copy(option = states.value.option.copy(imgCornerShape = event.imgCornerShape))
                updateOption(states.value.option)
            }

        }
    }

    private fun updateOption(option: Option) {
        viewModelScope.launch {
            mainRepository.upsertOption(option)
        }
    }

}