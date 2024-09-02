package com.mjdev.musicplayer.presentation.mainActivity


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.presentation.util.toMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _states = MutableStateFlow(MainScreenStates())
    val states = _states.asStateFlow()

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted = _isPermissionGranted.asStateFlow()

    init {
        // this is for getting initialMusics and setting up mediaController
        viewModelScope.launch {
            isPermissionGranted.collect { isGranted ->
                if (isGranted) {
                    launch(Dispatchers.IO) { mainRepository.cacheMusics() }
                    launch(Dispatchers.IO) {
                        val initialMusicItems = mainRepository.getInitialMusicItems()
                        if (initialMusicItems == null || initialMusicItems.musicItemId.isEmpty()) {

                            mainRepository.getAllMusicSortedBy()
                                .collect {
                                    _states.value = states.value.copy(mediaItems = it.toMediaItem())
                                }
                        } else {

                            initialMusicItems.musicItemId.map {
                                mainRepository.suspendGetMusicById(it)
                            }.also {musicItems->
                                Log.v("testmull",musicItems.toString())
                                _states.value = states.value.copy(
                                    mediaItems = musicItems.toMediaItem(),
                                    startItem = initialMusicItems.startMusicIndex,
                                    startPosition = initialMusicItems.startMusicPosition
                                )

                            }
//                            mainRepository.getAllMusicsById(initialMusicItems.musicItemId)
//                                .collectLatest { musicItems ->
//                                    _states.value = states.value.copy(
//                                        mediaItems = musicItems.toMediaItem(),
//                                        startItem = initialMusicItems.startMusicIndex,
//                                        startPosition = initialMusicItems.startMusicPosition
//                                    )
//
//                                }
                        }


                    }
                }
            }
        }

    }


    //    it.setMediaItems(states.value.mediaItems)
//    it.seekTo(states.value.startItem, states.value.startPosition)
//    it.prepare()
//    it.play()
//    it.stop()
    fun onPermissionChange(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }

//    override fun onCleared() {
//        super.onCleared()
//        val currentTimeline = states.value.mediaController!!.currentTimeline
//        val startMusicIndex = states.value.mediaController!!.currentMediaItemIndex
//        val startMusicPosition = states.value.mediaController!!.currentPosition
//        val mediaItems = (0 until currentTimeline.windowCount).map {
//            val window = Window()
//            currentTimeline.getWindow(it, window).mediaItem
//        }
//        Log.v("testDestroy", mediaItems.size.toString())
//
//        CoroutineScope(Dispatchers.IO).launch {
//            mainRepository.upsertInitialMusicItems(
//                InitialMusicItems(
//                    musicItemId = mediaItems.map { it.mediaId.toLong() },
//                    startMusicPosition = startMusicPosition,
//                    startMusicIndex = startMusicIndex
//                )
//            )
//            Log.v("testDestroy", mediaItems.size.toString())
//        }
//    }

    fun saveMediaItems(mediaController: MediaController) {
        val currentTimeline = mediaController.currentTimeline
        val startMusicIndex = mediaController.currentMediaItemIndex
        val startMusicPosition = mediaController.currentPosition
        val window = Timeline.Window()

        val mediaItems = (0 until currentTimeline.windowCount).map {
         currentTimeline.getWindow(it, window).mediaItem
        }

        CoroutineScope(Dispatchers.IO).launch {
            mainRepository.upsertInitialMusicItems(
                InitialMusicItems(
                    musicItemId = mediaItems.map { it.mediaId.toLong() },
                    startMusicPosition = startMusicPosition,
                    startMusicIndex = startMusicIndex
                )
            )

        }
    }
}