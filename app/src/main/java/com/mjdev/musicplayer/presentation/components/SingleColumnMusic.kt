package com.mjdev.musicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.YellowContainer
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.presentation.ui.theme.spacing

@Composable
fun SingleColumnMusic(
    modifier: Modifier = Modifier,
    imgSize: Dp = 48.dp,
    imgCornerShape: ImgCornerShape,
    isPlaying : Boolean,
    currentPlayingId : Long,
    musicItem: MusicItem,
    title: String,
    description: String,
    onToolClick: () -> Unit,
    onItemClick: () -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (musicItem.imgUri == null) {
            Box(
                modifier = Modifier
                    .size(imgSize)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(imgCornerShape.value())
                    )
                    .clip(RoundedCornerShape(imgCornerShape.value())),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = stringResource(
                        R.string.musicicon,
                    ) ,
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
                    .clip(RoundedCornerShape(imgCornerShape.value()))
                    .size(imgSize)

            )

        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaterialTheme.spacing.small),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = if( currentPlayingId == musicItem.musicId) YellowContainer else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if( currentPlayingId == musicItem.musicId) YellowContainer else    Color.White.copy(alpha = 0.5f)
            )
        }


        Row (verticalAlignment = Alignment.CenterVertically){

            PlayingMusicAnimation(isPlaying = isPlaying , modifier = Modifier.alpha(if (currentPlayingId ==musicItem.musicId) 1f else 0f))
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
            IconButton(onClick = onToolClick) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(id = R.string.option),
                    tint =  Color.White.copy(alpha = 0.5f)
                )
            }
        }

    }

}



