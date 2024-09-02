package com.mjdev.musicplayer.presentation.albumSrcreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.util.AlbumSortOrder

data class AlbumScreenState(
    val albums : List<List<MusicItem>> = emptyList(),
    val option : Option = Option(),
    val clickedAlbumIndex : Int  = 0,
    val allPlaylists : List<PlayList> = emptyList(),

    )
