package com.mjdev.musicplayer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun GridMusicView(
    modifier: Modifier = Modifier,
    gridNum: GridNum,
    type: String,
    imgCornerShape: ImgCornerShape,
    isPlaying: Boolean,
    currentPlayingId: Long,
    sortOrder: String,
    musicItems: List<MusicItem>,
    titles: List<String>,
    descriptions: List<String>,
    onSortClick: () -> Unit,
    onToolClick: (Int, MusicItem) -> Unit,
    onMusicItemClick: (index: Int, MusicItem) -> Unit,
) {
    val scope = rememberCoroutineScope()

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
                text = "${musicItems.size} $type",
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )

            Text(
                text = "$sortOrder ↓",
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .clickable {
                        onSortClick()
                        scope.launch {

                        }
                    }
                    .padding(MaterialTheme.spacing.small)
            )


        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(gridNum.value),
            verticalItemSpacing = MaterialTheme.spacing.extraSmall,
            modifier = Modifier
        ) {
            itemsIndexed(
                musicItems,
                key = { index, musicItem -> musicItem.musicId }) { index, musicItem ->
                if (gridNum.value > 1) {
                    MultipleColumnMusic(
                        modifier = Modifier.animateItem(),
                        musicItem = musicItem,
                        title = titles[index],
                        imgCornerShape = imgCornerShape,
                        description = descriptions[index],
                        isPlaying = isPlaying && musicItem.musicId == currentPlayingId,
                        currentPlayingId = currentPlayingId,
                        onToolClick = { onToolClick(index, musicItem) },
                        onItemClick = { onMusicItemClick(index, musicItem) })
                } else {
                    SingleColumnMusic(
                        modifier = Modifier.animateItem(),
                        musicItem = musicItem,
                        imgCornerShape = imgCornerShape,
                        title = titles[index],
                        isPlaying = isPlaying && musicItem.musicId == currentPlayingId,
                        currentPlayingId = currentPlayingId,
                        description = descriptions[index],
                        onToolClick = { onToolClick(index, musicItem) },
                        onItemClick = { onMusicItemClick(index, musicItem) })
                }

            }
        }


    }


}

