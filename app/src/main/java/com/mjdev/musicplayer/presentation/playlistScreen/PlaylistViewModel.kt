package com.mjdev.musicplayer.presentation.playlistScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder
import com.mjdev.musicplayer.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _states = MutableStateFlow(PlaylistStates())
    val states = _states.asStateFlow()

    init {

        viewModelScope.launch {
            mainRepository.getOption().flatMapLatest {
                _states.value = states.value.copy(option = it ?: Option())
                mainRepository.getAllPlayListsSortedBy(
                    it?.playlistSortOrder ?: PlayListSortOrder.None
                )
            }.collectLatest {playlists ->
                val result = playlists.map {playlist ->
                    mainRepository.suspendGetMusicsWithPlayListId(playlist.playlistName).first()
                }
                _states.value = states.value.copy(playlists = result)
            }
        }
    }

fun events(event: PlaylistEvents) {
    when (event) {

        is PlaylistEvents.SortChange -> {
            _states.value =
                states.value.copy(option = states.value.option.copy(playlistSortOrder = event.sort))
            viewModelScope.launch {
//                    getAllPlaylists(event.sort)
                mainRepository.upsertOption(states.value.option)
            }
        }

        is PlaylistEvents.NewPlaylistNameChange -> {
            _states.value = states.value.copy(newPlaylistName = event.value)
        }

        is PlaylistEvents.AddPlaylist -> {
            viewModelScope.launch {
                mainRepository.upsertPlayList(
                    PlayList(
                        states.value.newPlaylistName,
                        System.currentTimeMillis()
                    )
                )
            }
        }

        is PlaylistEvents.ClearPlaylist -> {
            viewModelScope.launch {
                event.playListWithMusics.musics.forEach {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            it.musicId,
                            event.playListWithMusics.playlist.playlistName
                        )
                    )

                }
            }
        }

        is PlaylistEvents.ClickPlaylistIndexChange -> {
            _states.value = states.value.copy(clickedPlaylistIndex = event.index)
        }

        is PlaylistEvents.DeleteFromDevice -> {
            viewModelScope.launch(Dispatchers.IO) {
                mainRepository.deletePlayList(event.playListWithMusics.playlist)
                _states.value = states.value.copy(
                    playlists = states.value.playlists.toMutableList().apply {
                        this.remove(event.playListWithMusics)
                    })

            }
        }

        is PlaylistEvents.AddToPlaylists -> {
            viewModelScope.launch {
                event.selectedPlaylists.forEachIndexed { index, selected ->
                    if (selected) {
                        states.value.playlists[states.value.clickedPlaylistIndex].musics.forEach { musicItem ->
                            mainRepository.upsertPlayListMusicCrossRef(
                                MusicPlayListCrossRef(
                                    musicId = musicItem.musicId,
                                    playlistName = states.value.playlists[index].playlist.playlistName
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


override fun onCleared() {
    super.onCleared()
    CoroutineScope(Dispatchers.IO).launch {
        mainRepository.upsertOption(states.value.option)
    }
}
}

