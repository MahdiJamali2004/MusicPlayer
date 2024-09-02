package com.mjdev.musicplayer.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.presentation.ui.theme.spacing


@Composable
fun SongToolSheet(
    modifier: Modifier = Modifier,
    musicItem: MusicItem,
    isDeleteEnable : Boolean,
    playlists: List<PlayList>,
    onClick: (SongTools) -> Unit,
    onDismiss : () -> Unit,
    addToFavorite: () -> Unit,
    removeFromFavorite: () -> Unit,
) {

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = MaterialTheme.spacing.small
                )
        ) {
            if (musicItem.imgUri == null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(MaterialTheme.spacing.medium)
                        )
                        .clip(RoundedCornerShape(MaterialTheme.spacing.medium)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = stringResource(
                            R.string.musicicon,
                        ),
                        tint =   Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                    )
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(musicItem.imgUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.music_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = musicItem.displayName,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.basicMarquee()

                )


                Text(
                    text = musicItem.artist,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
            IconButton(onClick = {
                if (playlists.any { it.playlistName == "favorite" }) removeFromFavorite() else addToFavorite()
            }) {
                Icon(
                    painter = if (playlists.any { it.playlistName == stringResource(R.string.favorite) }) painterResource(
                        id = R.drawable.ic_heart_fill
                    ) else painterResource(id = R.drawable.ic_heart_outline),
                    contentDescription = stringResource(R.string.playpausebutton),
                )


            }
        }
        HorizontalDivider()
        SongTools.entries.forEachIndexed loop@ { index, songTool ->
            if (!isDeleteEnable && songTool==SongTools.Delete)
                return@loop

            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(songTool)
                    onDismiss()
                }
                .padding(MaterialTheme.spacing.medium)
            ) {
                Icon(
                    painter = painterResource(id = songTool.icon),
                    contentDescription = songTool.name
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                Text(
                    text = songTool.title, fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )

            }
        }


    }

}

enum class SongTools(val title: String, val icon: Int) {
    Play("Play", R.drawable.ic_play),
    PlayNext("Play next", R.drawable.ic_play_next),
    AddToPlayingQueue("Add to playing queue", R.drawable.ic_add_to_queue),
    AddToPlaylist("Add to playlist", R.drawable.ic_add_circle),
    GoToAlbum("Go to album", R.drawable.ic_album_field),
    GoToArtist("Go to artist", R.drawable.ic_find_artist),
    Delete("Delete from device", R.drawable.ic_delete),
}