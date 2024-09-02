package com.mjdev.musicplayer.presentation.util

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.domain.model.MusicItem

fun List<MusicItem?>.toMediaItem(): List<MediaItem> {
    return this.filterNotNull().map { musicItem ->
        MediaItem.Builder()
//            .setUri(Uri.parse(musicItem.uri))
            .setMediaId(musicItem.musicId.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setComposer(musicItem.uri)
                    .setTitle(musicItem.displayName)
                    .setArtist(musicItem.artist)
                    .setAlbumTitle(musicItem.album)
                    .setAlbumArtist(musicItem.albumArtist)
                    .setArtworkUri(
                        if (musicItem.imgUri != null)
                            Uri.parse(
                                musicItem.imgUri
                            )
                        else
                            null
                    )
                    .build()
            ).build()

    }

}


fun List<MediaItem>.toMusicItem(): List<MusicItem> {
    return this.map {mediaItem ->
        MusicItem(
            musicId = mediaItem.mediaId.toLong(),
            displayName = mediaItem.mediaMetadata.title?.toString() ?: "Unknown",
            uri =  "",
            imgUri = mediaItem.mediaMetadata.artworkUri?.toString(),
            artist = mediaItem.mediaMetadata.artist?.toString() ?: "",
            albumArtist = mediaItem.mediaMetadata.albumArtist?.toString() ?: "",
            album = mediaItem.mediaMetadata.albumTitle?.toString() ?: "",
            duration = 0,
            size =  -1,
            dataAdded =0,
            year =0

        )
    }
}


fun MusicItem.toMediaItem(): MediaItem {
    return MediaItem.Builder()
//        .setUri(this.uri)
        .setMediaId(this.musicId.toString())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setComposer(this.uri)
                .setTitle(this.displayName)
                .setArtist(this.artist)
                .setArtworkUri(
                    if (this.imgUri != null)
                        Uri.parse(
                            this.imgUri
                        )
                    else
                        null
                )
                .setAlbumArtist(this.albumArtist)
                .build()
        ).build()

}


fun List<MusicItem>.getSumDurations(): Long {
    return if (this.isEmpty())
        0
    else {
        this.map { it.duration }.reduce { acc, l ->
            acc + l
        }

    }
}

fun Long.formattedTime(): String {
    val seconds = (this / 1000) % 60
    val minutes = (this / 1000) / 60
    return if (seconds - 9 < 0) {
        "$minutes:0$seconds"
    } else {
        "$minutes:$seconds"
    }
}
fun Long.formattedTimeWithHour():String {
    val seconds = this / 1000 % 60
    val minutes = this / 1000 / 60
    val hour =  this / 1000 / 60 / 60
    return "$hour:$minutes:$seconds"
}


fun Gradient.toBrush(): Brush {
    return when (this) {
        Gradient.None -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
        Gradient.Sanguine -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.LusciousLime -> Brush.verticalGradient(
            listOf(
                Color(firstColor),
                Color(secondColor)
            )
        )

        Gradient.PurpleLake -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.SweetMorning -> Brush.verticalGradient(
            listOf(
                Color(firstColor),
                Color(secondColor)
            )
        )

        Gradient.Quepal -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.Yosemite -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.Exotic -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.Celestial -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
        Gradient.MountainRock -> Brush.verticalGradient(
            listOf(
                Color(firstColor),
                Color(secondColor)
            )
        )
        Gradient.PlumPlate -> Brush.verticalGradient(listOf(Color(firstColor), Color(secondColor)))
    }
}



fun ImageTheme.toDrawable() {
    when (this) {
        ImageTheme.None -> drawable
        ImageTheme.B1 -> drawable
        ImageTheme.B2 -> drawable
        ImageTheme.B3 -> drawable
        ImageTheme.B4 -> drawable
        ImageTheme.B5 -> drawable
        ImageTheme.B6 -> drawable
        ImageTheme.B7 -> drawable
        ImageTheme.B8 -> drawable
        ImageTheme.B9 -> drawable
        ImageTheme.B10 -> drawable
        ImageTheme.B11 -> drawable
        ImageTheme.B12 -> drawable
        ImageTheme.B13 -> drawable
        ImageTheme.B14 -> drawable
        ImageTheme.B16 -> drawable
        ImageTheme.B17 -> drawable
        ImageTheme.B19 -> drawable
        ImageTheme.B20 -> drawable
        ImageTheme.B21 -> drawable
        ImageTheme.B22 -> drawable
        ImageTheme.B23 -> drawable
        ImageTheme.B25 -> drawable
        ImageTheme.B26 -> drawable
        ImageTheme.B27 -> drawable
    }
}