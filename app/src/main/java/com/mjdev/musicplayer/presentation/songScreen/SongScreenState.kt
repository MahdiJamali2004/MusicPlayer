package com.mjdev.musicplayer.presentation.songScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList

data class SongScreenState(
    val musics : List<MusicItem> = emptyList(),
    val option : Option = Option(),
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val toolMediaItemIndex : Int = 0,
    val isLoading  : Boolean = true,
    val allPlaylists : List<PlayList> = emptyList(),
    val deletedMusicUri : MusicItem? = null
)
