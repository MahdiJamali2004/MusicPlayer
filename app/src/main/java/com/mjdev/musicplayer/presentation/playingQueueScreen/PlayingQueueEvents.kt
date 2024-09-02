package com.mjdev.musicplayer.presentation.playingQueueScreen

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.media3.session.MediaController
import com.mjdev.musicplayer.domain.model.MusicItem


sealed class PlayingQueueEvents {
    data class RemoveFromQueue(val index : Int,val mediaController: MediaController) : PlayingQueueEvents()
    data class ToolMediaItemIndexChanged(val index: Int) : PlayingQueueEvents()
    data class DeleteFromDevice(
        val musicItem: MusicItem,
        val managedActivityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) : PlayingQueueEvents()
    data class DeleteFromDatabase(val musicItem: MusicItem) : PlayingQueueEvents()
    data object AddToFavorite : PlayingQueueEvents()
    data object RemoveFromFavorite : PlayingQueueEvents()
    data class AddToPlaylists(val selectedPlaylists: List<Boolean>) : PlayingQueueEvents()
}