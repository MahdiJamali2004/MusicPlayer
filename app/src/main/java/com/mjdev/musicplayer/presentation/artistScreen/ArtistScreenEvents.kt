package com.mjdev.musicplayer.presentation.artistScreen

import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.albumSrcreen.AlbumScreenEvents
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents

sealed class ArtistScreenEvents {
    data class SortChange(val sort: ArtistSortOrder) : ArtistScreenEvents()
    data class ClickedAlbumIndexChanged(val index: Int) : ArtistScreenEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : ArtistScreenEvents()
}