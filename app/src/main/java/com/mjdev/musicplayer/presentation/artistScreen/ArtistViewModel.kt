package com.mjdev.musicplayer.presentation.artistScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _states = MutableStateFlow(ArtistScreenState())
    val states = _states.asStateFlow()


    init {

        viewModelScope.launch {
            mainRepository.getOption().collectLatest {
                _states.value = states.value.copy(option = it ?: Option())
            }
        }

        viewModelScope.launch {
            mainRepository.getOption().flatMapLatest {
                _states.value = states.value.copy(option = it ?: Option())
                mainRepository.getArtistSortedBy(it?.artistSortOrder ?: ArtistSortOrder.None)
            }.collectLatest {
                _states.value = states.value.copy(artists = it)
            }
        }
        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }
    }

    fun events(event: ArtistScreenEvents) {
        when (event) {
            is ArtistScreenEvents.SortChange -> {
                _states.value = states.value.copy(
                    option = states.value.option.copy(artistSortOrder = event.sort)
                )
                viewModelScope.launch {
                    mainRepository.upsertOption(states.value.option)

                }
            }
            is ArtistScreenEvents.ClickedAlbumIndexChanged -> {
                _states.value = states.value.copy(clickedAlbumIndex = event.index)
            }

            is ArtistScreenEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected){
                            states.value.artists[states.value.clickedAlbumIndex].forEach {  musicItem ->
                                mainRepository.upsertPlayListMusicCrossRef(
                                    MusicPlayListCrossRef(
                                        musicId = musicItem.musicId,
                                        playlistName = states.value.allPlaylists[index].playlistName
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