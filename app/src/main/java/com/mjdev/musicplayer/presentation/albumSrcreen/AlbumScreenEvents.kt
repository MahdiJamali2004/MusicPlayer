package com.mjdev.musicplayer.presentation.albumSrcreen

import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.artistDetailScreen.ArtistDetailEvents
import com.mjdev.musicplayer.presentation.artistScreen.ArtistScreenEvents
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents

sealed class AlbumScreenEvents {
    data class SortChange(val sort: AlbumSortOrder) : AlbumScreenEvents()
    data class ClickedAlbumIndexChanged(val index: Int) : AlbumScreenEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : AlbumScreenEvents()

}