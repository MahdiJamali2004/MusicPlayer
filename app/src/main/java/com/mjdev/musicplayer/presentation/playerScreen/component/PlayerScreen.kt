package com.mjdev.musicplayer.presentation.playerScreen.component

import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.presentation.playerScreen.PlayerScreenEvents
import com.mjdev.musicplayer.presentation.playerScreen.PlayerScreenViewModel
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.PlayingQueueScreen
import com.mjdev.musicplayer.presentation.util.formattedTime
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerScreenViewModel = hiltViewModel(),
    mediaController: MediaController,
    navController: NavController
) {

    LaunchedEffect(Unit) {
        viewModel.events(PlayerScreenEvents.RepeatModeChange(mediaController.repeatMode))
        viewModel.events(PlayerScreenEvents.ShuffleChange(mediaController.shuffleModeEnabled))
    }
    val context = LocalContext.current
    val states by viewModel.states.collectAsState()
    var currentMediaItem by remember { mutableStateOf(mediaController.currentMediaItem!!.mediaMetadata) }
    var musicDuration by remember { mutableLongStateOf(mediaController.duration) }
    val animatePlayPause = remember { Animatable(50f) }
    val scope = rememberCoroutineScope()

    var playing by remember {
        mutableStateOf(mediaController.isPlaying)
    }
    val rotation = remember { Animatable(0f) }

    // Control the animation based on the state
    LaunchedEffect(playing) {
        if (playing) {
            scope.launch {
                while (playing) {
                    rotation.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(12000, easing = LinearEasing)
                    )
                    rotation.snapTo(0f)  // Reset to 0f to avoid lag
                }
            }

        } else {
            rotation.stop()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier, onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "close playerScreen",
                    tint = Color.White.copy(alpha = 0.5f),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PLAYING FROM",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = Color.White.copy(alpha = 0.5f),
                )
                Text(
                    text = currentMediaItem.albumTitle?.toString() ?: "Unknown",
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                )
            }

            //dummy
            IconButton(modifier = Modifier.alpha(0f), enabled = false, onClick = {  }) {
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        if (currentMediaItem.artworkUri == null) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(MaterialTheme.spacing.circle)
                    )
                    .border(
                        BorderStroke(
                            MaterialTheme.spacing.superExtraSmall,
                            MaterialTheme.colorScheme.onSurface
                        ),
                        CircleShape
                    )
                    .clip(RoundedCornerShape(MaterialTheme.spacing.circle))
                    .padding(MaterialTheme.spacing.extraLarge)
                    .rotate(rotation.value),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = stringResource(
                        R.string.musicicon,
                    ),
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentMediaItem.artworkUri)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.music_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .border(
                        BorderStroke(
                            MaterialTheme.spacing.superExtraSmall,
                            MaterialTheme.colorScheme.onSurface
                        ),
                        CircleShape
                    )
                    .clip(RoundedCornerShape(MaterialTheme.spacing.circle))
                    .size(300.dp)
                    .rotate(rotation.value)
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))


        var currentPosition by remember { mutableFloatStateOf(0f) }
        var currentPositionLong by remember { mutableLongStateOf(0) }
        var positionChangeBySlider by remember { mutableStateOf(false) }

        LaunchedEffect(mediaController.currentPosition) {
            if (mediaController.playbackState == Player.STATE_READY && !positionChangeBySlider) {
                currentPosition =
                    (mediaController.currentPosition.toFloat() / mediaController.contentDuration)
                currentPositionLong = mediaController.currentPosition
            }
        }
        LaunchedEffect(mediaController) {
            mediaController.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_READY) {
                        musicDuration = mediaController.contentDuration
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    currentMediaItem = mediaController.currentMediaItem!!.mediaMetadata
                    viewModel.events(PlayerScreenEvents.MusicItemChange(mediaController.currentMediaItem!!.mediaId.toLong()))


                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    playing = isPlaying
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    super.onRepeatModeChanged(repeatMode)
                    viewModel.events(PlayerScreenEvents.RepeatModeChange(repeatMode))
                }

            })

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (states.playlists.any { it.playlistName == "favorite" }) {
                        Toast.makeText(context, context.getString(R.string.remove_from_favorite), Toast.LENGTH_SHORT).show()
                        viewModel.events(PlayerScreenEvents.RemoveFromFavorite(mediaController.currentMediaItem!!.mediaId.toLong()))
                    } else {
                        Toast.makeText(context, context.getString(R.string.add_to_favorite), Toast.LENGTH_SHORT).show()
                        viewModel.events(PlayerScreenEvents.AddToFavorite(mediaController.currentMediaItem!!.mediaId.toLong()))
                    }
                }) {
                    Icon(
                        painter = if (states.playlists.any { it.playlistName == "favorite" })
                            painterResource(id = R.drawable.ic_heart_fill) else painterResource(id = R.drawable.ic_heart_outline),
                        contentDescription = "close playerScreen",
                        tint = Color.White,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = currentMediaItem.title?.toString() ?: "Unknown",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentMediaItem.artist?.toString() ?: "Unknown",
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { navController.navigate(PlayingQueueScreen) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = "close playerScreen",
                        tint = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(value = currentPosition,
                    colors = SliderColors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.5f),
                        activeTickColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                        disabledThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        disabledActiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledInactiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    ),
                    onValueChange = {
                        Log.v("valueChanging", "$it")
                        positionChangeBySlider = true
                        currentPosition = it

                    },
                    onValueChangeFinished = {
                        mediaController.seekTo((currentPosition * mediaController.contentDuration).toLong())
                        positionChangeBySlider = false
                    })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -MaterialTheme.spacing.medium)
                        .padding(horizontal = MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentPositionLong.formattedTime(),
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                    Text(
                        text = musicDuration.formattedTime(),
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(
                        start = MaterialTheme.spacing.medium,
                        end = MaterialTheme.spacing.medium,
                        top = MaterialTheme.spacing.medium
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(onClick = {
                    when (states.repeatMode) {
                        Player.REPEAT_MODE_OFF -> {
                            mediaController.repeatMode = Player.REPEAT_MODE_ALL
                            viewModel.events(PlayerScreenEvents.RepeatModeChange(Player.REPEAT_MODE_ALL))
                            Toast.makeText(
                                context,
                                context.getString(R.string.repeat_current_queue),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        Player.REPEAT_MODE_ALL -> {
                            mediaController.repeatMode = Player.REPEAT_MODE_ONE
                            viewModel.events(PlayerScreenEvents.RepeatModeChange(Player.REPEAT_MODE_ONE))
                            Toast.makeText(context,   context.getString(R.string.repeat_current), Toast.LENGTH_SHORT).show()
                        }

                        Player.REPEAT_MODE_ONE -> {
                            mediaController.repeatMode = Player.REPEAT_MODE_OFF
                            viewModel.events(PlayerScreenEvents.RepeatModeChange(Player.REPEAT_MODE_OFF))
                            Toast.makeText(context,
                                context.getString(R.string.repeat_off), Toast.LENGTH_SHORT).show()

                        }
                    }

                }) {
                    Icon(
                        imageVector = when (states.repeatMode) {
                            Player.REPEAT_MODE_OFF -> Icons.Rounded.SyncAlt
                            Player.REPEAT_MODE_ALL -> Icons.Rounded.Repeat
                            Player.REPEAT_MODE_ONE -> Icons.Rounded.RepeatOne
                            else -> return@IconButton
                        },
                        contentDescription = stringResource(R.string.repeatmode),
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = {
                    mediaController.seekToPreviousMediaItem()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = stringResource(R.string.skipprevious),
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(modifier = Modifier.size(animatePlayPause.value.dp), onClick = {
                    scope.launch {
                        animatePlayPause.animateTo(56f, animationSpec = tween(200))
                        animatePlayPause.animateTo(50f, animationSpec = tween(200))
                    }
                    if (playing) {
                        mediaController.pause()
                    } else {
                        mediaController.play()
                    }
                }) {
                    Icon(
                        painter = if (playing) painterResource(id = R.drawable.ic_pause_circle) else painterResource(
                            id = R.drawable.ic_play_circle
                        ),
                        contentDescription = stringResource(R.string.playpause),
                        modifier = Modifier.size(animatePlayPause.value.dp)
                    )
                }
                IconButton(onClick = {
                    mediaController.seekToNextMediaItem()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = stringResource(R.string.skipnext),
                        modifier = Modifier.size(32.dp)
                    )
                }


                IconButton(onClick = {
                    Toast.makeText(context, if (!states.shuffle) context.getString(R.string.shuffle_on) else context.getString(R.string.shuffle_off) , Toast.LENGTH_SHORT).show()
                    mediaController.shuffleModeEnabled = !states.shuffle
                    viewModel.events(PlayerScreenEvents.ShuffleChange(!states.shuffle))
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = stringResource(R.string.shuffle),
                        tint = if (states.shuffle) MaterialTheme.colorScheme.onSurface else Color.White.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

        }

    }
}


