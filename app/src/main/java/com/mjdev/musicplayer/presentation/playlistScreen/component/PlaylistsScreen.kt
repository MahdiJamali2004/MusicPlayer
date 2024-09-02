package com.mjdev.musicplayer.presentation.playlistScreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder
import com.mjdev.musicplayer.presentation.albumSrcreen.AlbumScreenEvents
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.MultipleColumnMusic
import com.mjdev.musicplayer.presentation.components.SingleColumnMusic
import com.mjdev.musicplayer.presentation.components.SortBottomSheet
import com.mjdev.musicplayer.presentation.playlistScreen.PlaylistEvents
import com.mjdev.musicplayer.presentation.playlistScreen.PlaylistViewModel
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.PlaylistDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaController: MediaController,
    viewModel: PlaylistViewModel = hiltViewModel(),
) {
    val states by viewModel.states.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    var sortSheet by remember { mutableStateOf(false) }
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }



    if (addToPlaylist) {
        AddToPlaylistDialog(
            playlists = states.playlists.map { it.playlist },
            onConfirm = { viewModel.events(PlaylistEvents.AddToPlaylists(it)) },
            onDismiss = { addToPlaylist = false })

    }

    var addPlaylist by remember { mutableStateOf(false) }
    if (addPlaylist) {
        AddPlaylistDialog(value = states.newPlaylistName,
            onValueChange = { viewModel.events(PlaylistEvents.NewPlaylistNameChange(it)) },
            onDismiss = { addPlaylist = false },
            onConfirm = { viewModel.events(PlaylistEvents.AddPlaylist) })
    }
    if (sortSheet || toolSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { sortSheet = false; toolSheet = false }) {
            if (sortSheet) {
                SortBottomSheet(
                    sortEntries = PlayListSortOrder.entries.map { it.name },
                    selectedItemIndex = states.option.playlistSortOrder.ordinal,
                    onDismiss = { sortSheet = false },
                    onClick = {
                        viewModel.events(
                            PlaylistEvents.SortChange(
                                PlayListSortOrder.valueOf(
                                    it
                                )!!
                            )
                        )
                    }
                )
            }
            if (toolSheet) {
                PlaylistToolSheet(
                    playListWithMusics = states.playlists[states.clickedPlaylistIndex],
                    onDismiss = { toolSheet = false },
                    onClick = {
                        when (it) {
                            PlaylistTools.Play -> {
                                if (states.playlists[states.clickedPlaylistIndex].musics.isNotEmpty()){
                                    mediaController.setMediaItems(states.playlists[states.clickedPlaylistIndex].musics.toMediaItem())
                                    mediaController.prepare()
                                    mediaController.play()
                                }
                            }

                            PlaylistTools.PlayNext -> {
                                mediaController.addMediaItems(
                                    mediaController.currentMediaItemIndex + 1,
                                    states.playlists[states.clickedPlaylistIndex].musics.toMediaItem()
                                )

                            }

                            PlaylistTools.AddToPlayingQueue -> {
                                mediaController.addMediaItems(states.playlists[states.clickedPlaylistIndex].musics.toMediaItem())
                            }

                            PlaylistTools.AddToPlaylist -> {
                                addToPlaylist = true
                            }

                            PlaylistTools.ClearPlaylist -> {
                                viewModel.events(PlaylistEvents.ClearPlaylist(states.playlists[states.clickedPlaylistIndex]))
                            }

                            PlaylistTools.Delete -> {
                                viewModel.events(PlaylistEvents.DeleteFromDevice(states.playlists[states.clickedPlaylistIndex]))
                            }
                        }
                    }

                )
            }
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = MaterialTheme.spacing.extraSmall,
                    start = MaterialTheme.spacing.small,
                    end = MaterialTheme.spacing.small
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${states.playlists.size} playlists",
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = { addPlaylist = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_playlist),
                        tint = Color.White.copy(alpha = 0.5f),
                    )
                }
                Text(
                    text = "${states.option.playlistSortOrder.name} ↓",
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clickable { sortSheet = true }
                        .padding(MaterialTheme.spacing.small)
                )
            }


        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(states.option.gridNum.value),
            verticalItemSpacing = MaterialTheme.spacing.extraSmall,
            modifier = Modifier
        ) {
            itemsIndexed(
                states.playlists,
                key = { _, playListWithMusics -> playListWithMusics.playlist.playlistName }) { index, playlistMusic ->
                if (states.option.gridNum.value > 1) {
                    MultipleColumnMusic(
                        modifier = Modifier.animateItem(),
                        musicItem = playlistMusic.musics.firstOrNull() ?: MusicItem(),
                        title = playlistMusic.playlist.playlistName,
                        imgCornerShape = states.option.imgCornerShape,
                        description = "${playlistMusic.musics.size} Songs",
                        currentPlayingId = -1,
                        isPlaying = false,
                        onToolClick = {
                            viewModel.events(PlaylistEvents.ClickPlaylistIndexChange(index))
                            toolSheet = true
                        },
                        onItemClick = { navController.navigate(PlaylistDetailScreen(playlistMusic.playlist.playlistName)) })
                } else {
                    SingleColumnMusic(
                        modifier = Modifier.animateItem(),
                        musicItem = playlistMusic.musics.firstOrNull() ?: MusicItem(),
                        imgCornerShape = states.option.imgCornerShape,
                        title = playlistMusic.playlist.playlistName,
                        currentPlayingId = -1,
                        isPlaying = false,
                        description = "${playlistMusic.musics.size} Songs",
                        onToolClick = {
                            viewModel.events(PlaylistEvents.ClickPlaylistIndexChange(index))
                            toolSheet = true
                        },
                        onItemClick = { navController.navigate(PlaylistDetailScreen(playlistMusic.playlist.playlistName)) })
                }

            }
        }


    }
}