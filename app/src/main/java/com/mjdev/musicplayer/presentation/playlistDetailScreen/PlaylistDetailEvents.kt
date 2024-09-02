package com.mjdev.musicplayer.presentation.playlistDetailScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents

sealed class PlaylistDetailEvents {
    data class ToolMediaItemIndexChanged(val index : Int) : PlaylistDetailEvents()
    data object AddToFavorite : PlaylistDetailEvents()
    data object RemoveFromFavorite : PlaylistDetailEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : PlaylistDetailEvents()
}