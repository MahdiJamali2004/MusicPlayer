package com.mjdev.musicplayer.presentation.albumDetailScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.presentation.albumDetailScreen.AlbumDetailEvents
import com.mjdev.musicplayer.presentation.albumDetailScreen.AlbumDetailViewModel
import com.mjdev.musicplayer.presentation.components.AddToPlaylistDialog
import com.mjdev.musicplayer.presentation.components.SingleColumnMusic
import com.mjdev.musicplayer.presentation.components.SongToolSheet
import com.mjdev.musicplayer.presentation.components.SongTools
import com.mjdev.musicplayer.presentation.playlistDetailScreen.PlaylistDetailEvents
import com.mjdev.musicplayer.presentation.songScreen.SongScreenEvents
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.formattedTime
import com.mjdev.musicplayer.presentation.util.getSumDurations
import com.mjdev.musicplayer.presentation.util.toMediaItem

@ExperimentalMaterial3Api
@Composable
fun AlbumDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    mediaController: MediaController,
    navController: NavController
) {
    val states by viewModel.states.collectAsState()
    val lazyColumnState = rememberLazyListState()
    val bottomSheetState = rememberModalBottomSheetState()
    var toolSheet by remember { mutableStateOf(false) }
    var addToPlaylist by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(mediaController.isPlaying) }
    LaunchedEffect(mediaController) {
        mediaController.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playing = isPlaying
            }

        })

    }
    if (addToPlaylist ) {
        AddToPlaylistDialog(
            playlists = states.allPlaylists,
            onConfirm = { viewModel.events(AlbumDetailEvents.AddToPlaylists(it)) },
            onDismiss = {addToPlaylist=false})

    }

    if (toolSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { toolSheet = false }) {


            SongToolSheet(
                musicItem = states.musics[states.toolMediaItemIndex],
                playlists = states.toolClickPlaylists,
                onDismiss = { toolSheet = false },
                isDeleteEnable = false,
                onClick = {
                    when (it) {
                        SongTools.Play -> {
                            mediaController.setMediaItems(
                                states.musics.toMediaItem(),
                                states.toolMediaItemIndex,
                                0
                            )
                            mediaController.prepare()
                            mediaController.play()
                        }

                        SongTools.PlayNext -> {
                            mediaController.addMediaItem(
                                mediaController.currentMediaItemIndex + 1,
                                states.musics[states.toolMediaItemIndex].toMediaItem()
                            )
                        }

                        SongTools.AddToPlayingQueue -> {
                            mediaController.addMediaItem(states.musics[states.toolMediaItemIndex].toMediaItem())
                        }

                        SongTools.AddToPlaylist -> {
                            addToPlaylist = true
                        }

                        SongTools.GoToAlbum -> {
                            navController.navigate(
                                com.mjdev.musicplayer.presentation.util.AlbumDetailScreen(
                                    states.musics[states.toolMediaItemIndex].musicId
                                )
                            )
                        }

                        SongTools.GoToArtist -> {
                            navController.navigate(ArtistDetailScreen(states.musics[states.toolMediaItemIndex].musicId))
                        }

                        SongTools.Delete -> {
                        }
                    }
                },
                addToFavorite = { viewModel.events(AlbumDetailEvents.AddToFavorite) },
                removeFromFavorite = { viewModel.events(AlbumDetailEvents.RemoveFromFavorite) })
        }

    }


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            state = lazyColumnState,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            item {
                Column (
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        val randomImg = states.musics.firstOrNull()?.imgUri
                        if (randomImg == null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(MaterialTheme.spacing.small)
                                    )
                                    .clip(RoundedCornerShape(MaterialTheme.spacing.extraLarge))
                                    .padding(MaterialTheme.spacing.small),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_music),
                                    contentDescription = stringResource(
                                        R.string.musicicon,
                                    ),
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(states.musics.firstOrNull()?.imgUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.music_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(MaterialTheme.spacing.small))
                                    .aspectRatio(1f)
                                    .weight(1f)

                            )
                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                        Column(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = states.musics.firstOrNull()?.album ?: "Unknown Album",
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = states.musics.firstOrNull()?.artist ?: "Unknown Artist",
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color =  Color.White.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${states.musics.firstOrNull()?.year} • ${states.musics.size} • ${
                                    states.musics.getSumDurations().formattedTime()
                                }",
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = Color.White.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                        }

                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    //buttons row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //PlayAll button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(MaterialTheme.spacing.small)
                                )
                                .clip(RoundedCornerShape(MaterialTheme.spacing.small))
                                .clickable {
                                    mediaController.shuffleModeEnabled = false
                                    mediaController.setMediaItems(
                                        states.musics.toMediaItem(),
                                        0,
                                        0
                                    )
                                    mediaController.prepare()
                                    mediaController.play()
                                }
                                .padding(vertical = MaterialTheme.spacing.small + MaterialTheme.spacing.superExtraSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = stringResource(R.string.playarrow),
                                tint = Color.White.copy(alpha = 0.5f),
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                            Text(
                                text = stringResource(R.string.play_all),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )

                        }
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium + MaterialTheme.spacing.extraSmall))
                        //shuffle button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(MaterialTheme.spacing.small)
                                )
                                .clip(RoundedCornerShape(MaterialTheme.spacing.small))

                                .clickable {
                                    mediaController.shuffleModeEnabled = true
                                    mediaController.setMediaItems(states.musics.toMediaItem())
                                    mediaController.prepare()
                                    mediaController.play()
                                }
                                .padding(vertical = MaterialTheme.spacing.small + MaterialTheme.spacing.superExtraSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shuffle),
                                contentDescription = stringResource(id = R.string.shuffle),
                                tint = Color.White.copy(alpha = 0.5f),
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                            Text(
                                text = stringResource(id = R.string.shuffle).uppercase(),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )

                        }
                    }


                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
            itemsIndexed(states.musics , key = {index, musicItem ->  musicItem.musicId}) { index, musicItem ->
                SingleColumnMusic(
                    modifier = Modifier.animateItem(),
                    imgCornerShape = ImgCornerShape.Round,
                    musicItem = musicItem,
                    title = musicItem.displayName,
                    description = "${musicItem.artist} • ${musicItem.duration.formattedTime()}",
                    currentPlayingId = mediaController.currentMediaItem!!.mediaId.toLong()  ,
                    isPlaying = mediaController.currentMediaItem!!.mediaId.toLong() == musicItem.musicId && playing,
                    onToolClick = {
                        viewModel.events(AlbumDetailEvents.ToolMediaItemIndexChanged(index))
                        toolSheet = true
                    },
                    onItemClick = {
                        mediaController.setMediaItems(states.musics.toMediaItem(), index, 0)
                        mediaController.prepare()
                        mediaController.play()
                    })

            }
        }

    }


}

