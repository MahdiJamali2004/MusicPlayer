package com.mjdev.musicplayer.presentation.playlistDetailScreen

import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.PlayList

data class PlaylistDetailStates(
    val playListWithMusics: PlayListWithMusics = PlayListWithMusics(
        PlayList("", System.currentTimeMillis()),
        emptyList()
    ),
    val toolClickPlaylists : List<PlayList> = emptyList(),
    val toolMediaItemIndex : Int = 0,
    val allPlaylists : List<PlayList> = emptyList(),

    )
