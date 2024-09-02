package com.mjdev.musicplayer.musicService

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.mjdev.musicplayer.Application.Companion.MUSIC_CHANNEL_NOTIFICATION
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import com.mjdev.musicplayer.domain.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {
    @Inject
    lateinit var mainRepository: MainRepository
    private var mediaSession: MediaSession? = null

    companion object {
        const val CLOSE_BUTTON_COMMAND = "close_button"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession


    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomCallback())
            .setCustomLayout(customCommandLayout())
            .build()
        mediaSession?.player?.repeatMode = Player.REPEAT_MODE_ALL
     DefaultMediaNotificationProvider.Builder(applicationContext)
         .setChannelId(MUSIC_CHANNEL_NOTIFICATION)
            .build()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {

        val player = mediaSession?.player!!
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED
        ) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            player.stop()
            mediaSession?.run {
                player.stop()
                player.release()
                mediaSession = null
                stopSelf()
            }


        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            mediaSession = null
        }
        super.onDestroy()
    }


    private inner class CustomCallback() : MediaSession.Callback {

        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                .add(SessionCommand(CLOSE_BUTTON_COMMAND, Bundle.EMPTY))
                .build()
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(sessionCommands)
                .build()
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            // to rebuild the playable MediaItem.
            Log.v("testLog", mediaItems.map { it.mediaMetadata.composer }.toString())
            val updatedMediaItems = mediaItems.map {
                it.buildUpon().setUri(it.mediaMetadata.composer.toString()).setMediaId(it.mediaId).build()
            }.toMutableList()
            return Futures.immediateFuture(updatedMediaItems)
        }


        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (customCommand.customAction == CLOSE_BUTTON_COMMAND) {
                saveMediaItems(player = session.player)
                session.player.stop()

            }
            return Futures.immediateFuture(
                SessionResult(SessionResult.RESULT_SUCCESS)
            )
        }

    }

    private fun customCommandLayout(): ImmutableList<CommandButton> {

        val closeButton = CommandButton.Builder()
            .setDisplayName("Stop playing")
            .setIconResId(R.drawable.ic_close)
            .setSessionCommand(SessionCommand(CLOSE_BUTTON_COMMAND, Bundle()))
            .build()

        return ImmutableList.of(closeButton)
    }

    private fun saveMediaItems(player: Player) {
        val currentTimeline = player.currentTimeline
        val startMusicIndex = player.currentMediaItemIndex
        val startMusicPosition = player.currentPosition
        val mediaItemIds = (0 until currentTimeline.windowCount).map {
            val window = Timeline.Window()
            val mediaItem = currentTimeline.getWindow(it, window).mediaItem
            mediaItem.mediaId.toLong()
        }

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            mainRepository.upsertInitialMusicItems(
                InitialMusicItems(
                    musicItemId = mediaItemIds,
                    startMusicIndex = startMusicIndex,
                    startMusicPosition = startMusicPosition
                )
            )
        }

    }

}

