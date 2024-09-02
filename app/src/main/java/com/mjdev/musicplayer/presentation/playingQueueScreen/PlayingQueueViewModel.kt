@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mjdev.musicplayer.presentation.playingQueueScreen

import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.presentation.songScreen.SongScreenState
import com.mjdev.musicplayer.presentation.util.toMusicItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PlayingQueueViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _states = MutableStateFlow(SongScreenState())
    val states = _states.asStateFlow()

    init {


        viewModelScope.launch {
            mainRepository.getAllPlayListsSortedBy().collectLatest {
                _states.value = states.value.copy(allPlaylists = it)
            }
        }

    }

    fun events(event: PlayingQueueEvents) {
        when (event) {

            is PlayingQueueEvents.RemoveFromQueue -> {
                event.mediaController.removeMediaItem(event.index)
                getCurrentMusics(event.mediaController)
            }

            is PlayingQueueEvents.ToolMediaItemIndexChanged -> {

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

            PlayingQueueEvents.AddToFavorite -> {
                viewModelScope.launch {
                    mainRepository.upsertPlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            playlistName = "favorite"
                        )
                    )

                }
            }

            PlayingQueueEvents.RemoveFromFavorite -> {
                viewModelScope.launch {
                    mainRepository.deletePlayListMusicCrossRef(
                        MusicPlayListCrossRef(
                            musicId = states.value.musics[states.value.toolMediaItemIndex].musicId,
                            "favorite"
                        )
                    )
                }
            }

            is PlayingQueueEvents.DeleteFromDevice -> {
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

            is PlayingQueueEvents.DeleteFromDatabase -> {
                viewModelScope.launch {
                    mainRepository.deleteMusicFromDatabase(event.musicItem)
                }
            }

            is PlayingQueueEvents.AddToPlaylists -> {
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

    fun getCurrentMusics(mediaController: MediaController) {
        val currentTimeline = mediaController.currentTimeline
        val window = Timeline.Window()
        val mediaItems = (0 until currentTimeline.windowCount).map {
            currentTimeline.getWindow(it, window).mediaItem
        }



        _states.value = states.value.copy(musics = mediaItems.toMusicItem())
    }

}