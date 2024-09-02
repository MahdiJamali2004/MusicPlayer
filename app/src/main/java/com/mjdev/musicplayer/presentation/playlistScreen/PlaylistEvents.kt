package com.mjdev.musicplayer.presentation.playlistScreen

import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.util.PlayListSortOrder
import com.mjdev.musicplayer.presentation.artistScreen.ArtistScreenEvents

sealed class PlaylistEvents {
    data class SortChange(val sort: PlayListSortOrder) : PlaylistEvents()
//    data class DeletePlaylist(val playlist: PlayList) : PlaylistEvents()
    data class NewPlaylistNameChange(val value: String) : PlaylistEvents()
    data object AddPlaylist : PlaylistEvents()
    data class ClickPlaylistIndexChange(val index : Int) : PlaylistEvents()
    data class DeleteFromDevice(val playListWithMusics: PlayListWithMusics ) : PlaylistEvents()
    data class ClearPlaylist(val playListWithMusics: PlayListWithMusics) : PlaylistEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : PlaylistEvents()



}