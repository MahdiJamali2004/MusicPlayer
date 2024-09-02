@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mjdev.musicplayer.presentation.songScreen

import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _states = MutableStateFlow(SongScreenState())
    val states = _states.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.cacheMusics()
        }
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getOption().collectLatest {
                _states.value = states.value.copy(option = it ?: Option())
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.getOption().flatMapLatest {
                _states.value = states.value.copy(option = it ?: Option())
                mainRepository.getAllMusicSortedBy(it?.musicSortOrder ?: MusicSortOrder.None)
            }.collectLatest {
                if (it.isEmpty())
                    _states.value = states.value.copy(musics = it)
                else
                    _states.value = states.value.copy(musics = it, isLoading = false)
            }

        }
        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }

    }

    fun events(event: SongScreenEvents) {
        when (event) {
            is SongScreenEvents.SortChange -> {
                _states.value =
                    states.value.copy(option = states.value.option.copy(musicSortOrder = event.sort))
                viewModelScope.launch {
                    mainRepository.upsertOption(option = states.value.option)
                }
            }

            is SongScreenEvents.ToolMediaItemIndexChanged -> {

                _states.value = states.value.copy(toolMediaItemIndex = event.index)
                viewModelScope.launch {
                    mainRepository.getPlayListsWithMusicIdSortedBy(states.value.musics[event.index].musicId)
                        .collectLatest {
                            _states.value = states.value.copy(
                                toolMediaItemIndex = event.index,
                                toolClickPlaylists = it.firstOrNull()?.playLists ?: emptyList()
                            )
                        }
                }
            }

            SongScreenEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            SongScreenEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            "favorite"
                        )
                    )
                }
            }

            is SongScreenEvents.DeleteFromDevice -> {
                viewModelScope.launch {
                    val intentSender = mainRepository.deleteMusicFromDevice(event.musicItem)
                    if (intentSender != null) {
                        event.managedActivityResultLauncher.launch(
                            IntentSenderRequest.Builder(
                                intentSender
                            ).build()
                        )
                        _states.value = states.value.copy(deletedMusicUri = event.musicItem)
                    }
                }
            }

            is SongScreenEvents.DeleteFromDatabase -> {
                viewModelScope.launch {
                    mainRepository.deleteMusicFromDatabase(event.musicItem)
                }
            }

            is SongScreenEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected) {
                            mainRepository.upsertPlayListMusicCrossRef(
                                MusicPlayListCrossRef(
                                    musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                                    playlistName = states.value.allPlaylists[index].playlistName
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("testClear", "cleared")
        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.upsertOption(states.value.option)
        }
    }
}