package com.mjdev.musicplayer.presentation.playlistScreen

import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.Option

data class PlaylistStates(
    val playlists : List<PlayListWithMusics> = emptyList(),
    val option: Option = Option(),
    val newPlaylistName : String = "",
    val clickedPlaylistIndex : Int = 0,
)
