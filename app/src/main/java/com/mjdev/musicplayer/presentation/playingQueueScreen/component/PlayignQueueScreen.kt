@file:OptIn(ExperimentalMaterial3Api::class)

package com.mjdev.musicplayer.presentation.playingQueueScreen.component

import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.example.compose.YellowContainer
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.SongToolSheet
import com.mjdev.musicplayer.presentation.components.SongTools
import com.mjdev.musicplayer.presentation.playingQueueScreen.PlayingQueueEvents
import com.mjdev.musicplayer.presentation.playingQueueScreen.PlayingQueueViewModel
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem
import kotlinx.coroutines.launch

@Composable
fun PlayingQueueScreen(
    modifier: Modifier = Modifier,
    mediaController: MediaController,
    navController: NavController,
    viewModel: PlayingQueueViewModel = hiltViewModel()
) {


    val state by viewModel.states.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    val lazyColumnState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }
    var currentMusicId by remember {
        mutableLongStateOf(
            mediaController.currentMediaItem?.mediaId?.toLong() ?: -1
        )
    }
    var currentMusicIndex by remember { mutableIntStateOf(mediaController.currentMediaItemIndex) }
    var deleteRequest by remember {
        mutableStateOf<ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>?>(null)
    }
    deleteRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    viewModel.events(
                        PlayingQueueEvents.DeleteFromDevice(
                            state.deletedMusicUri ?: return@rememberLauncherForActivityResult,
                            deleteRequest!!
                        )
                    )
                }
                viewModel.events(PlayingQueueEvents.DeleteFromDatabase(state.musics[state.toolMediaItemIndex]))
                viewModel.getCurrentMusics(mediaController)
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.getCurrentMusics(mediaController)
    }
    LaunchedEffect(mediaController) {
        mediaController.addListener(object : Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                currentMusicId = mediaController.currentMediaItem?.mediaId?.toLong() ?: -1
                currentMusicIndex = mediaController.currentMediaItemIndex
            }

        })

    }
    if (addToPlaylist && state.allPlaylists.isNotEmpty()) {
        AddToPlaylistDialog(
            playlists = state.allPlaylists,
            onConfirm = { viewModel.events(PlayingQueueEvents.AddToPlaylists(it)) },
            onDismiss = { addToPlaylist = false })

    }

    if (toolSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { toolSheet = false }) {
            SongToolSheet(
                musicItem = state.musics[state.toolMediaItemIndex],
                playlists = state.toolClickPlaylists,
                onDismiss = { toolSheet = false },
                isDeleteEnable = true,
                onClick = {
                    when (it) {
                        SongTools.Play -> {
                            mediaController.setMediaItems(
                                state.musics.toMediaItem(),
                                state.toolMediaItemIndex,
                                0
                            )
                            mediaController.prepare()
                            mediaController.play()
                        }

                        SongTools.PlayNext -> {
                            mediaController.addMediaItem(
                                mediaController.currentMediaItemIndex + 1,
                                state.musics[state.toolMediaItemIndex].toMediaItem()
                            )
                            viewModel.getCurrentMusics(mediaController)
                        }

                        SongTools.AddToPlayingQueue -> {
                            mediaController.addMediaItem(state.musics[state.toolMediaItemIndex].toMediaItem())
                            viewModel.getCurrentMusics(mediaController)
                        }

                        SongTools.AddToPlaylist -> {
                            addToPlaylist = true
                        }

                        SongTools.GoToAlbum -> {
                            navController.navigate(AlbumDetailScreen(state.musics[state.toolMediaItemIndex].musicId))
                        }

                        SongTools.GoToArtist -> {
                            navController.navigate(ArtistDetailScreen(state.musics[state.toolMediaItemIndex].musicId))
                        }

                        SongTools.Delete -> {
                            viewModel.events(
                                PlayingQueueEvents.DeleteFromDevice(
                                    state.musics[state.toolMediaItemIndex],
                                    deleteRequest!!
                                )
                            )

                        }
                    }
                },
                addToFavorite = { viewModel.events(PlayingQueueEvents.AddToFavorite) },
                removeFromFavorite = { viewModel.events(PlayingQueueEvents.RemoveFromFavorite) })

        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Up next • $currentMusicIndex/${state.musics.size}",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = Color.White.copy(alpha = 0.5f)
            )
            IconButton(onClick = {
                scope.launch {
                    lazyColumnState.animateScrollToItem(currentMusicIndex)
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.MyLocation,
                    contentDescription = stringResource(
                        R.string.go_to_current_song
                    ),
                    tint = YellowContainer
                )
            }

        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        LazyColumn(
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            itemsIndexed(
                state.musics,
                key = { index, _ -> index }) { index, musicItem ->

                PlayingQueueItem(
                    modifier = Modifier.animateItem(),
                    imgCornerShape = ImgCornerShape.Round,
                    isSelected = index == currentMusicIndex,
                    isRemoveEnable = state.musics.size != 1,
                    musicItem = musicItem,
                    title = musicItem.displayName,
                    description = musicItem.artist,
                    onRemoveMusic = {
                        viewModel.events(
                            PlayingQueueEvents.RemoveFromQueue(
                                index,
                                mediaController
                            )
                        )
                    },
                    onToolClick = {
                        viewModel.events(PlayingQueueEvents.ToolMediaItemIndexChanged(index))
                        toolSheet = true
                    },
                    onItemClick = {
                        mediaController.seekTo(index, 0)
                        viewModel.getCurrentMusics(mediaController)
                    })
            }

        }
    }
}


