package com.mjdev.musicplayer.presentation.artistDetailScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList

data class ArtistDetailStates(
    val musics : List<MusicItem> = emptyList(),
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val toolMediaItemIndex : Int = 0,
    val allPlaylists : List<PlayList> = emptyList(),

    )
