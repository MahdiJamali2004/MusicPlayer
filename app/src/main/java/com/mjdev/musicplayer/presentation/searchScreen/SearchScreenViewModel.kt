package com.mjdev.musicplayer.presentation.searchScreen

import androidx.activity.result.IntentSenderRequest
import androidx.compose.ui.focus.FocusState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _states = MutableStateFlow(SearchScreenStates())
    val states = _states.asStateFlow()

    init {
        viewModelScope.launch {
            mainRepository.getOption().collectLatest {
                _states.value = states.value.copy(option = it ?: Option())
            }
        }
        viewModelScope.launch {
            mainRepository.getAllMusicSortedBy(MusicSortOrder.DateAdded)
                .collectLatest {
                    _states.value = states.value.copy(musics = it, filteredMusics = it)
                }
        }
        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }
    }

    fun onSearch(query: String) {
        if (query.isEmpty()){
            _states.value = states.value.copy(
                query = query,
                filteredMusics = states.value.musics)
        }else{
            _states.value = states.value.copy(
                query = query,
                filteredMusics = states.value.musics.filter {
                    it.displayName.lowercase().contains(query)
                })

        }
    }

    fun clearSearch() {
        _states.value = states.value.copy(
            query = "",
            filteredMusics = states.value.musics
        )
    }

    fun onFocusStateChange(focusState: FocusState) {
        _states.value =
            states.value.copy(isHintVisible = !focusState.isFocused && states.value.query.isBlank())
    }

    fun events(event : SearchScreenEvents){
        when(event){
            SearchScreenEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.filteredMusics[states.value.toolMediaItemIndex].musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            SearchScreenEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.filteredMusics[states.value.toolMediaItemIndex].musicId,
                            "favorite"
                        )
                    )
                }
            }
            is SearchScreenEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected) {
                            mainRepository.upsertPlayListMusicCrossRef(
                                MusicPlayListCrossRef(
                                    musicId = states.value.filteredMusics[states.value.toolMediaItemIndex].musicId,
                                    playlistName = states.value.allPlaylists[index].playlistName
                                )
                            )
                        }
                    }
                }
            }
            is SearchScreenEvents.DeleteFromDevice -> {
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

            is SearchScreenEvents.DeleteFromDatabase -> {
                viewModelScope.launch {
                    mainRepository.deleteMusicFromDatabase(event.musicItem)
                }
            }
            is SearchScreenEvents.ToolMediaItemIndexChanged -> {

                _states.value = states.value.copy(toolMediaItemIndex = event.index)
                viewModelScope.launch {
                    mainRepository.getPlayListsWithMusicIdSortedBy(states.value.filteredMusics[event.index].musicId)
                        .collectLatest {
                            _states.value = states.value.copy(
                                toolMediaItemIndex = event.index,
                                toolClickPlaylists = it.firstOrNull()?.playLists ?: emptyList()
                            )
                        }
                }
            }
        }
    }
}