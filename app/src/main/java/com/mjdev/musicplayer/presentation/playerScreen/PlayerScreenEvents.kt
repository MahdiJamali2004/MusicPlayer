package com.mjdev.musicplayer.presentation.playerScreen

import com.mjdev.musicplayer.domain.model.MusicItem

sealed class PlayerScreenEvents {
    data class RepeatModeChange(val mode : Int) : PlayerScreenEvents()
    data class ShuffleChange(val value : Boolean) : PlayerScreenEvents()
    data class MusicItemChange(val mediaId : Long) : PlayerScreenEvents()
    data class AddToFavorite(val musicId : Long) : PlayerScreenEvents()
    data class RemoveFromFavorite(val musicId : Long) : PlayerScreenEvents()

}