package com.mjdev.musicplayer.presentation.albumSrcreen.component

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.albumSrcreen.AlbumScreenEvents
import com.mjdev.musicplayer.presentation.albumSrcreen.AlbumViewModel
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.AlbumAndArtistToolSheet
import com.mjdev.musicplayer.presentation.components.AlbumArtistTools
import com.mjdev.musicplayer.presentation.components.GridMusicView
import com.mjdev.musicplayer.presentation.components.SortBottomSheet
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaController: MediaController,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val state by viewModel.states.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    var sortSheet by remember { mutableStateOf(false) }
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }

    if (addToPlaylist) {
        AddToPlaylistDialog(
            playlists = state.allPlaylists,
            onConfirm = { viewModel.events(AlbumScreenEvents.AddToPlaylists(it)) },
            onDismiss = {addToPlaylist=false})

    }

    if (sortSheet || toolSheet){

        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { sortSheet = false; toolSheet = false }) {
            if (sortSheet) {
                SortBottomSheet(
                    sortEntries = AlbumSortOrder.entries.map { it.name },
                    selectedItemIndex = state.option.albumSortOrder.ordinal,
                    onDismiss = {sortSheet = false},
                    onClick = { viewModel.events(AlbumScreenEvents.SortChange(AlbumSortOrder.valueOf(it)!!)) }
                )
            }
            if (toolSheet){
                AlbumAndArtistToolSheet(
                    musicItem = state.albums[state.clickedAlbumIndex].first(),
                    onDismiss = {toolSheet = false},
                    onClick = {
                        when(it){
                            AlbumArtistTools.Play -> {
                                mediaController.setMediaItems(state.albums[state.clickedAlbumIndex].toMediaItem())
                                mediaController.prepare()
                                mediaController.play()
                            }
                            AlbumArtistTools.PlayNext -> {
                                mediaController.addMediaItems(mediaController.currentMediaItemIndex + 1,state.albums[state.clickedAlbumIndex].toMediaItem())
                            }
                            AlbumArtistTools.AddToPlayingQueue -> {
                                mediaController.addMediaItems(state.albums[state.clickedAlbumIndex].toMediaItem())

                            }
                            AlbumArtistTools.AddToPlaylist -> {
                                addToPlaylist = true
                            }
                        }
                    }
                )
            }
        }
    }


                GridMusicView(
                    modifier = modifier
                        .fillMaxSize(),
                    sortOrder = state.option.albumSortOrder.name,
                    imgCornerShape = state.option.imgCornerShape,
                    type = "Albums",
                    musicItems = state.albums.map { it[0] },
                    titles = state.albums.map {
                        it[0].album
                    },
                    descriptions = state.albums.map {
                        "${it[0].artist} • ${it.size} Song"
                    },
                    onSortClick = { sortSheet = true },
                    onToolClick = {index , _ ->
                        viewModel.events(AlbumScreenEvents.ClickedAlbumIndexChanged(index))
                        toolSheet = true
                    },
                    isPlaying =false,
                    currentPlayingId = -1,
                    gridNum = state.option.gridNum,
                    onMusicItemClick = { index,musicItem ->
                        navController.navigate(AlbumDetailScreen(musicItem.musicId))
                    }
                )

}

