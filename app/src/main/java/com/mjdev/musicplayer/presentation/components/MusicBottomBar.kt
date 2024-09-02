package com.mjdev.musicplayer.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MusicBottomBar(
    modifier: Modifier = Modifier,
    mediaController: MediaController,
    onClick: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var metaData by remember { mutableStateOf(mediaController.currentMediaItem?.mediaMetadata) }
    var playing by remember {
        mutableStateOf(mediaController.isPlaying)
    }

    var position by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(mediaController) {
        mediaController.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                metaData = mediaController.currentMediaItem!!.mediaMetadata
            }


            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playing = isPlaying
            }

        })

    }

    LaunchedEffect(true) {
        while (true) {
            position = mediaController.currentPosition.toFloat() / mediaController.contentDuration
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = MaterialTheme.spacing.medium,
                    topEnd = MaterialTheme.spacing.medium
                )
            )
            .clickable { onClick() } ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(
                        topStart = MaterialTheme.spacing.medium,
                        topEnd = MaterialTheme.spacing.medium
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = MaterialTheme.spacing.medium,
                        topEnd = MaterialTheme.spacing.medium
                    )
                )
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.small,
                    top = MaterialTheme.spacing.small,
                    bottom = MaterialTheme.spacing.extraSmall
                )
        ) {
            if (metaData?.artworkUri == null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(MaterialTheme.spacing.small)
                        )
                        .clip(RoundedCornerShape(MaterialTheme.spacing.small)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = stringResource(
                            R.string.musicicon,
                        ),
                        tint =  Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(metaData?.artworkUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.music_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = metaData?.title?.toString() ?: "UNKNOWN",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .basicMarquee()
                )
                Text(
                    text = metaData?.artist?.toString() ?: "UNKNOWN",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,

                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
            IconButton(onClick = {
                mediaController.seekToPrevious()
            }) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = stringResource(id = R.string.skipprevious)
                )
            }
            IconButton(onClick = {
                scope.launch {
                    if (playing) {
                        mediaController.pause()
                    } else {
                        mediaController.prepare()
                        mediaController.play()
                    }

                }
            }) {
                Icon(
                    imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.playpausebutton),
                )
            }
            IconButton(onClick = {
                mediaController.seekToNext()
            }) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = stringResource(id = R.string.option)
                )
            }


        }
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.superExtraSmall),
            trackColor = Color.White.copy(alpha = 0.2f),
            progress = { position }, strokeCap = StrokeCap.Round
        )

    }
}