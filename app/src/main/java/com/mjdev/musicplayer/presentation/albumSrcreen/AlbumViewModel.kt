package com.mjdev.musicplayer.presentation.albumSrcreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
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
class AlbumViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _states = MutableStateFlow(AlbumScreenState())
    val states = _states.asStateFlow()


    init {

        viewModelScope.launch {
            mainRepository.getOption().flatMapLatest {
                _states.value = states.value.copy(option = it ?: Option())
                mainRepository.getAlbumSortedBy(it?.albumSortOrder ?: AlbumSortOrder.None)
            }.collectLatest {
                _states.value = states.value.copy(albums = it)
            }
        }
        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }
    }

    fun events(event: AlbumScreenEvents) {
        when (event) {
            is AlbumScreenEvents.SortChange -> {
                _states.value = states.value.copy(
                    option = states.value.option.copy(albumSortOrder = event.sort)
                )
                viewModelScope.launch {
                    mainRepository.upsertOption(states.value.option)

                }
            }

            is AlbumScreenEvents.ClickedAlbumIndexChanged -> {
                _states.value = states.value.copy(clickedAlbumIndex = event.index)
            }

            is AlbumScreenEvents.AddToPlaylists -> {
                viewModelScope.launch {
                    event.selectedPlaylists.forEachIndexed { index, selected ->
                        if (selected){
                            states.value.albums[states.value.clickedAlbumIndex].forEach {  musicItem ->
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