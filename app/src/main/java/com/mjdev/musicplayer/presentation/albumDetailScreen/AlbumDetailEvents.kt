package com.mjdev.musicplayer.presentation.albumDetailScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents

sealed class AlbumDetailEvents {
    data class ToolMediaItemIndexChanged(val index : Int) : AlbumDetailEvents()
    data object AddToFavorite : AlbumDetailEvents()
    data object RemoveFromFavorite : AlbumDetailEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : AlbumDetailEvents()
}