package com.mjdev.musicplayer.presentation.albumDetailScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList

data class AlbumDetailStates(
    val musics : List<MusicItem> = emptyList(),
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val toolMediaItemIndex : Int = 0,
    val allPlaylists : List<PlayList> = emptyList(),

    )
