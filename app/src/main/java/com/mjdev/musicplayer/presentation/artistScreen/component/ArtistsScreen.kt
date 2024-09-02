package com.mjdev.musicplayer.presentation.artistScreen.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.presentation.albumSrcreen.AlbumScreenEvents
import com.mjdev.musicplayer.presentation.util.SearchScreen
import com.mjdev.musicplayer.presentation.artistScreen.ArtistScreenEvents
import com.mjdev.musicplayer.presentation.artistScreen.ArtistViewModel
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.AlbumAndArtistToolSheet
import com.mjdev.musicplayer.presentation.components.AlbumArtistTools
import com.mjdev.musicplayer.presentation.components.CustomTopAppBar
import com.mjdev.musicplayer.presentation.components.GridMusicView
import com.mjdev.musicplayer.presentation.components.MusicBottomBar
import com.mjdev.musicplayer.presentation.components.OptionDropDownMenu
import com.mjdev.musicplayer.presentation.components.SortBottomSheet
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaController: MediaController,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val state by viewModel.states.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    var sortSheet by remember { mutableStateOf(false) }
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }

    if (addToPlaylist) {
        AddToPlaylistDialog(
            playlists = state.allPlaylists,
            onConfirm = { viewModel.events(ArtistScreenEvents.AddToPlaylists(it)) },
            onDismiss = {addToPlaylist=false})

    }
    if (sortSheet || toolSheet){

        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { sortSheet = false; toolSheet = false }) {
            if (sortSheet) {
                SortBottomSheet(
                    sortEntries = ArtistSortOrder.entries.map { it.name },
                    onDismiss = {sortSheet = false},
                    selectedItemIndex = state.option.artistSortOrder.ordinal,
                    onClick = { viewModel.events(ArtistScreenEvents.SortChange(ArtistSortOrder.valueOf(it)!!)) }
                )
            }
            if (toolSheet){
                AlbumAndArtistToolSheet(
                    musicItem = state.artists[state.clickedAlbumIndex].first(),
                    onDismiss = {toolSheet = false},
                    onClick = {
                        when(it){
                            AlbumArtistTools.Play -> {
                                mediaController.setMediaItems(state.artists[state.clickedAlbumIndex].toMediaItem())
                                mediaController.prepare()
                                mediaController.play()
                            }
                            AlbumArtistTools.PlayNext -> {
                                mediaController.addMediaItems(mediaController.currentMediaItemIndex + 1,state.artists[state.clickedAlbumIndex].toMediaItem())
                            }
                            AlbumArtistTools.AddToPlayingQueue -> {
                                mediaController.addMediaItems(state.artists[state.clickedAlbumIndex].toMediaItem())

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
                    modifier = Modifier
                        .fillMaxSize(),
                    sortOrder = state.option.artistSortOrder.name,
                    imgCornerShape = state.option.imgCornerShape,
                    type = "Artists",
                    musicItems = state.artists.map { it[0] },
                    titles = state.artists.map {
                        it[0].artist
                    },
                    descriptions = state.artists.map {
                        "${it[0].albumArtist} • ${it.size} Song"
                    },
                    onSortClick = { sortSheet = true },
                    onToolClick = {index , _ ->
                        viewModel.events(ArtistScreenEvents.ClickedAlbumIndexChanged(index))
                        toolSheet = true
                    },
                    gridNum = state.option.gridNum,
                   isPlaying = false,
                    currentPlayingId = -1,
                    onMusicItemClick = {index, musicItem ->
                        navController.navigate(ArtistDetailScreen(musicItem.musicId))
                    }
                )


}

