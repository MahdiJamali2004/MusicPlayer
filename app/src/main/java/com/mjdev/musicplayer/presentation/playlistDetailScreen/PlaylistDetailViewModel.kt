package com.mjdev.musicplayer.presentation.playlistDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.presentation.util.PlaylistDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _states = MutableStateFlow(PlaylistDetailStates())
    val states = _states.asStateFlow()

    init {
        val name = savedStateHandle.toRoute<PlaylistDetailScreen>().playlistName
        viewModelScope.launch {
            mainRepository.getMusicsWithPlayListIdSortedBy(name).collectLatest {
                if (it.firstOrNull() != null) {
                    _states.value = states.value.copy(playListWithMusics = it.first() )

                }
            }
        }
        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }
    }
    fun events(event: PlaylistDetailEvents) {
        when (event) {
            is PlaylistDetailEvents.ToolMediaItemIndexChanged -> {

                _states.value = states.value.copy(toolMediaItemIndex = event.index)
                viewModelScope.launch {
                    mainRepository.getPlayListsWithMusicIdSortedBy(states.value.playListWithMusics.musics[event.index].musicId)
                        .collectLatest {
                            _states.value = states.value.copy(
                                toolMediaItemIndex = event.index,
                                toolClickPlaylists = it.firstOrNull()?.playLists ?: emptyList()
                            )
                        }
                }
            }

            PlaylistDetailEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.playListWithMusics.musics[states.value.toolMediaItemIndex].musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            PlaylistDetailEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.playListWithMusics.musics[states.value.toolMediaItemIndex].musicId,
                            "favorite"
                        )
                    )
                }
            }

            is PlaylistDetailEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected){
                            mainRepository.upsertPlayListMusicCrossRef(MusicPlayListCrossRef(
                                musicId =  states.value.playListWithMusics.musics[states.value.toolMediaItemIndex].musicId,
                                playlistName = states.value.allPlaylists[index].playlistName
                            ))
                        }
                    }
                }
            }
        }
    }


}

