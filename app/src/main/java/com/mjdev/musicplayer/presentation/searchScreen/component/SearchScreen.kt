@file:OptIn(ExperimentalMaterial3Api::class)

package com.mjdev.musicplayer.presentation.searchScreen.component

import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.SingleColumnMusic
import com.mjdev.musicplayer.presentation.components.SongToolSheet
import com.mjdev.musicplayer.presentation.components.SongTools
import com.mjdev.musicplayer.presentation.searchScreen.SearchScreenEvents
import com.mjdev.musicplayer.presentation.searchScreen.SearchScreenViewModel
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.toMediaItem

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    mediaController: MediaController,
    viewModel: SearchScreenViewModel = hiltViewModel(),
    navController: NavController
) {
    val states by viewModel.states.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    var currentMediaItem by remember { mutableStateOf(mediaController.currentMediaItem!!) }

    val focusManager = LocalFocusManager.current
    var deleteRequest by remember {
        mutableStateOf<ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>?>(null)
    }
    deleteRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    viewModel.events(
                        SearchScreenEvents.DeleteFromDevice(
                            states.deletedMusicUri ?: return@rememberLauncherForActivityResult,
                            deleteRequest!!
                        )
                    )
                }
                viewModel.events(SearchScreenEvents.DeleteFromDatabase(states.filteredMusics[states.toolMediaItemIndex]))
            }
        }
    )

    var playing by remember { mutableStateOf(mediaController.isPlaying) }
    LaunchedEffect(mediaController) {
        mediaController.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playing = isPlaying
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                currentMediaItem = mediaController.currentMediaItem!!
            }
        })


    }
    if (addToPlaylist && states.allPlaylists.isNotEmpty()) {
        AddToPlaylistDialog(
            playlists = states.allPlaylists,
            onConfirm = { viewModel.events(SearchScreenEvents.AddToPlaylists(it)) },
            onDismiss = { addToPlaylist = false })

    }
    if (toolSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { toolSheet = false }) {
            SongToolSheet(
                musicItem = states.filteredMusics[states.toolMediaItemIndex],
                playlists = states.toolClickPlaylists,
                onDismiss = { toolSheet = false },
                isDeleteEnable = true,
                onClick = {
                    when (it) {
                        SongTools.Play -> {
                            mediaController.setMediaItems(
                                states.filteredMusics.toMediaItem(),
                                states.toolMediaItemIndex,
                                0
                            )
                            mediaController.prepare()
                            mediaController.play()
                        }

                        SongTools.PlayNext -> {
                            mediaController.addMediaItem(
                                mediaController.currentMediaItemIndex + 1,
                                states.filteredMusics[states.toolMediaItemIndex].toMediaItem()
                            )
                        }

                        SongTools.AddToPlayingQueue -> {
                            mediaController.addMediaItem(states.filteredMusics[states.toolMediaItemIndex].toMediaItem())
                        }

                        SongTools.AddToPlaylist -> {
                            addToPlaylist = true
                        }

                        SongTools.GoToAlbum -> {
                            navController.navigate(AlbumDetailScreen(states.filteredMusics[states.toolMediaItemIndex].musicId))
                        }

                        SongTools.GoToArtist -> {
                            navController.navigate(ArtistDetailScreen(states.filteredMusics[states.toolMediaItemIndex].musicId))
                        }

                        SongTools.Delete -> {
                            viewModel.events(
                                SearchScreenEvents.DeleteFromDevice(
                                    states.filteredMusics[states.toolMediaItemIndex],
                                    deleteRequest!!
                                )
                            )

                        }
                    }
                },
                addToFavorite = { viewModel.events(SearchScreenEvents.AddToFavorite) },
                removeFromFavorite = { viewModel.events(SearchScreenEvents.RemoveFromFavorite) })

        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(MaterialTheme.spacing.medium)
                    )
                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                    .clickable {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                    .padding(horizontal = MaterialTheme.spacing.superExtraSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(id = R.string.search),
                        tint = Color.White.copy(alpha = 0.5f),
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                    BasicTextField(
                        singleLine = true,
                        value = if (states.isHintVisible) stringResource(id = R.string.search) else states.query,
                        onValueChange = {
                            viewModel.onSearch(it)
                        },
                        textStyle = TextStyle(color = if (states.isHintVisible) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                viewModel.onFocusStateChange(it)
                            }
                    )

                }
                IconButton(onClick = { viewModel.clearSearch(); focusManager.clearFocus(); keyboardController?.hide() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.clearsearch),
                        tint = Color.White.copy(alpha = 0.5f),
                    )
                }
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))

            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {

            itemsIndexed(states.filteredMusics , key = {_, musicItem -> musicItem.musicId  }) { index, musicItem ->
                SingleColumnMusic(
                    modifier = Modifier.animateItem(),
                    imgCornerShape = states.option.imgCornerShape,
                    musicItem = musicItem,
                    title = musicItem.displayName,
                    currentPlayingId = mediaController.currentMediaItem!!.mediaId.toLong(),
                    isPlaying = currentMediaItem.mediaId.toLong() == musicItem.musicId && playing,
                    description = musicItem.album,
                    onToolClick = {
                        viewModel.events(SearchScreenEvents.ToolMediaItemIndexChanged(index))
                        toolSheet = true
                    },
                    onItemClick = {
                        mediaController.setMediaItems(states.filteredMusics.toMediaItem(), index, 0)
                        mediaController.prepare()
                        mediaController.play()
                    })
            }
        }

    }

}