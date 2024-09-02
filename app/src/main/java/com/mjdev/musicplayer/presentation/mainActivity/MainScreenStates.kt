package com.mjdev.musicplayer.presentation.mainActivity

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import com.mjdev.musicplayer.domain.model.InitialMusicItems

data class MainScreenStates(
    val mediaItems : List<MediaItem>  = emptyList(),
    val startPosition : Long = 0,
    val startItem : Int= 0,
)

