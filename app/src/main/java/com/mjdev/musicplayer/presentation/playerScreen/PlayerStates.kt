package com.mjdev.musicplayer.presentation.playerScreen

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList

data class PlayerStates(
    val repeatMode: Int = Player.REPEAT_MODE_ALL,
    val shuffle: Boolean = false,
    val playlists: List<PlayList> = emptyList()
)
