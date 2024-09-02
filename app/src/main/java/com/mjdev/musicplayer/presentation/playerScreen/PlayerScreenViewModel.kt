@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mjdev.musicplayer.presentation.playerScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.presentation.util.PlayerScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerScreenViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _states = MutableStateFlow(PlayerStates())
    val states = _states.asStateFlow()


    init {
        val id = savedStateHandle.toRoute<PlayerScreen>().musicItemId
        savedStateHandle[CURRENT_ID] = id

        viewModelScope.launch {
            savedStateHandle.getStateFlow(CURRENT_ID,-1).flatMapLatest {
                mainRepository.getPlayListsWithMusicIdSortedBy(it.toLong())
            }.collectLatest {musicWithPlayLists ->
                _states.value = states.value.copy(playlists = musicWithPlayLists.firstOrNull()?.playLists ?: emptyList())
            }

        }

    }

    fun events(event: PlayerScreenEvents) {
        when (event) {
            is PlayerScreenEvents.RepeatModeChange -> {
                _states.value = states.value.copy(repeatMode = event.mode)
            }

            is PlayerScreenEvents.ShuffleChange -> {
                _states.value = states.value.copy(shuffle = event.value)
            }
            is PlayerScreenEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = event.musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            is PlayerScreenEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(musicId = event.musicId, "favorite")
                    )
                }
            }

            is PlayerScreenEvents.MusicItemChange -> {
                savedStateHandle[CURRENT_ID] = event.mediaId
            }
        }
    }

    companion object{
        private const val CURRENT_ID = "currentId"
    }
}