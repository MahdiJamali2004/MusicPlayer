package com.mjdev.musicplayer.presentation.playingQueueScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList

data class PlayingQueueStates(
    val musics : List<MusicItem> = emptyList(),
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val toolMediaItemIndex : Int = 0,
    val allPlaylists : List<PlayList> = emptyList(),
    val deletedMusicUri : MusicItem? = null
)
