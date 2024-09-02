package com.mjdev.musicplayer.presentation.artistScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.util.AlbumSortOrder

data class ArtistScreenState(
    val artists : List<List<MusicItem>> = emptyList(),
    val option : Option = Option(),
    val clickedAlbumIndex : Int  = 0,
    val allPlaylists : List<PlayList> = emptyList(),
)
