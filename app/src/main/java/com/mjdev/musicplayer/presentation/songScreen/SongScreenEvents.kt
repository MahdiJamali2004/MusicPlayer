package com.mjdev.musicplayer.presentation.songScreen

import android.content.IntentSender
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.playerScreen.PlayerScreenEvents

sealed class SongScreenEvents {
    data class SortChange(val sort: MusicSortOrder) : SongScreenEvents()
    data class ToolMediaItemIndexChanged(val index: Int) : SongScreenEvents()
    data class DeleteFromDevice(
        val musicItem: MusicItem,
        val managedActivityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) : SongScreenEvents()
    data class DeleteFromDatabase(val musicItem: MusicItem) : SongScreenEvents()
    data object AddToFavorite : SongScreenEvents()
    data object RemoveFromFavorite : SongScreenEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : SongScreenEvents()
}