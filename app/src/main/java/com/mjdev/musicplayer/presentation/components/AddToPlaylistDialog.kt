package com.mjdev.musicplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import kotlin.math.max

@Composable
fun AddToPlaylistDialog(
    modifier: Modifier = Modifier,
    playlists: List<PlayList>,
    onConfirm: (List<Boolean>) -> Unit,
    onDismiss: () -> Unit,
) {

    var selectedPlaylists = remember { mutableStateListOf(*Array(playlists.size) { false }) }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(MaterialTheme.spacing.medium)
                )
        ) {
            Text(
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.medium,
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.small
                ),
                text = stringResource(R.string.add_to),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )

            LazyColumn(modifier = Modifier.heightIn(max = 256.dp)) {
                itemsIndexed(playlists){index, playList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPlaylists[index] = !selectedPlaylists[index]
                            }
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.extraSmall
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(MaterialTheme.spacing.small)
                                    )
                                    .clip(RoundedCornerShape(MaterialTheme.spacing.small)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_music),
                                    contentDescription = stringResource(
                                        R.string.musicicon,
                                    ),
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier
                                )
                            }
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                            Text(text = playList.playlistName)
                        }


                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (selectedPlaylists[index]) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    CircleShape
                                )
                                .padding(if (selectedPlaylists[index]) MaterialTheme.spacing.extraSmall else 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedPlaylists[index]) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = stringResource(
                                        R.string.check
                                    ),
                                    tint = MaterialTheme.colorScheme.primaryContainer
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.AddCircleOutline,
                                    contentDescription = stringResource(R.string.add),
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }


                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.small),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), onClick = onDismiss
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    enabled = selectedPlaylists.any { it },
                    onClick = { onConfirm(selectedPlaylists);onDismiss() }
                ) {
                    Text(text = "Confirm")
                }
            }
        }
    }

}