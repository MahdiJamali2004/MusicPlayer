package com.mjdev.musicplayer.presentation.artistDetailScreen

import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents

sealed class ArtistDetailEvents {
    data class ToolMediaItemIndexChanged(val index : Int) : ArtistDetailEvents()
    data object AddToFavorite : ArtistDetailEvents()
    data object RemoveFromFavorite : ArtistDetailEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : ArtistDetailEvents()
}