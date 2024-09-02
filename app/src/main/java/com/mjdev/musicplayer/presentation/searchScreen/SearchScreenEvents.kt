package com.mjdev.musicplayer.presentation.searchScreen

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.mjdev.musicplayer.domain.model.MusicItem

sealed class SearchScreenEvents {
    data class ToolMediaItemIndexChanged(val index: Int) : SearchScreenEvents()
    data class DeleteFromDevice(
        val musicItem: MusicItem,
        val managedActivityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) : SearchScreenEvents()
    data class DeleteFromDatabase(val musicItem: MusicItem) : SearchScreenEvents()
    data object AddToFavorite : SearchScreenEvents()
    data object RemoveFromFavorite : SearchScreenEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : SearchScreenEvents()
}