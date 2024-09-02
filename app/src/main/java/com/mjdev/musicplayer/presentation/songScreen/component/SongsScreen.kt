package com.mjdev.musicplayer.presentation.songScreen.component

import android.app.Activity.RESULT_OK
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.GridMusicView
import com.mjdev.musicplayer.presentation.components.SongToolSheet
import com.mjdev.musicplayer.presentation.components.SongTools
import com.mjdev.musicplayer.presentation.components.SortBottomSheet
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents
import com.mjdev.musicplayer.presentation.songScreen.SongViewModel
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaController: MediaController,
    viewModel: SongViewModel = hiltViewModel()
) {
    val state by viewModel.states.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    var sortSheet by remember { mutableStateOf(false) }
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(mediaController.isPlaying) }
    var deleteRequest by remember {
        mutableStateOf<ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>?>(null)
    }
     deleteRequest =  rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult ={
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q ){
                        viewModel.events(SongScreenEvents.DeleteFromDevice(state.deletedMusicUri ?:return@rememberLauncherForActivityResult,deleteRequest!!))
                    }
                    viewModel.events(SongScreenEvents.DeleteFromDatabase(state.musics[state.toolMediaItemIndex]))
                }
            }
        )

    LaunchedEffect(mediaController) {
        mediaController.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playing = isPlaying
            }

        })

    }
    if (addToPlaylist && state.allPlaylists.isNotEmpty()) {
        AddToPlaylistDialog(
            playlists = state.allPlaylists,
            onConfirm = { viewModel.events(SongScreenEvents.AddToPlaylists(it)) },
            onDismiss = { addToPlaylist = false })

    }

    if (sortSheet || toolSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { sortSheet = false; toolSheet = false }) {
            if (sortSheet) {
                SortBottomSheet(
                    sortEntries = MusicSortOrder.entries.map { it.name },
                    onDismiss = { sortSheet = false },
                    selectedItemIndex = state.option.musicSortOrder.ordinal,
                    onClick = {
                        viewModel.events(
                            SongScreenEvents.SortChange(
                                MusicSortOrder.valueOf(
                                    it
                                )!!
                            )
                        )

                    }
                )
            }
            if (toolSheet) {

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
                            }

                            SongTools.AddToPlayingQueue -> {
                                mediaController.addMediaItem(state.musics[state.toolMediaItemIndex].toMediaItem())
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
                                viewModel.events(SongScreenEvents.DeleteFromDevice(state.musics[state.toolMediaItemIndex],deleteRequest!!))

                            }
                        }
                    },
                    addToFavorite = { viewModel.events(SongScreenEvents.AddToFavorite) },
                    removeFromFavorite = { viewModel.events(SongScreenEvents.RemoveFromFavorite) })
            }
        }
    }
    Log.v("loadingState", state.isLoading.toString())
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {
        GridMusicView(
            modifier = modifier
                .fillMaxSize(),
            sortOrder = state.option.musicSortOrder.name,
            imgCornerShape = state.option.imgCornerShape,
            type = "Sounds",
            musicItems = state.musics,
            titles = state.musics.map { musicItem -> musicItem.displayName },
            descriptions = state.musics.map { musicItem ->
                "${musicItem.artist} • ${
                    formatedTime(
                        musicItem.duration
                    )
                }"
            },
            onSortClick = { sortSheet = true },
            onToolClick = { index, _ ->
                viewModel.events(SongScreenEvents.ToolMediaItemIndexChanged(index))
                toolSheet = true
            },
            isPlaying = playing,
            currentPlayingId = mediaController.currentMediaItem?.mediaId?.toLong() ?: -1,
            gridNum = state.option.gridNum,
            onMusicItemClick = { index, _ ->
                mediaController.setMediaItems(state.musics.toMediaItem(), index, 0)
                mediaController.prepare()
                mediaController.play()
            }
        )

    }


}

fun formatedTime(time: Long): String {
    val seconds = (time / 1000) % 60
    val minute = (time / 1000) / 60
    return "$minute:$seconds"
}
