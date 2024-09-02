package com.mjdev.musicplayer.presentation.albumDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _states = MutableStateFlow(AlbumDetailStates())
    val states = _states.asStateFlow()

    init {
        val id = savedStateHandle.toRoute<AlbumDetailScreen>().musicItemId
        val musicItem = MutableStateFlow<MusicItem?>(null)
        mainRepository.getMusicById(id)
            .onEach {
                musicItem.value = it
            }.launchIn(viewModelScope)
        viewModelScope.launch {
            musicItem.collect {
                if (it != null) {
                    getSongsOfAlbum(it)
                }
            }
        }

        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }
    }

    fun events(event: AlbumDetailEvents) {
        when (event) {
            is AlbumDetailEvents.ToolMediaItemIndexChanged -> {

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

            AlbumDetailEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            AlbumDetailEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            "favorite"
                        )
                    )
                }
            }


            is AlbumDetailEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected){
                            mainRepository.upsertPlayListMusicCrossRef(MusicPlayListCrossRef(
                                musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                                playlistName = states.value.allPlaylists[index].playlistName
                            ))
                        }
                    }
                }
            }
        }
    }

    private suspend fun getSongsOfAlbum(musicItem: MusicItem) {
        mainRepository.getAlbumSortedBy()
            .collect {

                val result = it.filter { albums ->
                    albums.first().album == musicItem.album
                }
                _states.value = states.value.copy(musics = result.first())
            }
    }
}