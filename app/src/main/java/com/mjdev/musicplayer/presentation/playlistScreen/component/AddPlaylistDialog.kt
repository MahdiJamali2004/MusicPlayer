package com.mjdev.musicplayer.presentation.playlistScreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.mjdev.musicplayer.presentation.ui.theme.spacing

@Composable
fun AddPlaylistDialog(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    RoundedCornerShape(MaterialTheme.spacing.medium)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.Start)
                    .padding(
                    start = MaterialTheme.spacing.large,
                    top = MaterialTheme.spacing.large,
                    end = MaterialTheme.spacing.large,
                    bottom = MaterialTheme.spacing.small
                ),
                text = "NewPlaylist...",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )

            HorizontalDivider(Modifier.padding(vertical = MaterialTheme.spacing.small))
            TextField(value = value,
                onValueChange = onValueChange,
                label = {
                    Text(text = "Playlist name")
                })

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
                    enabled = value.isNotBlank(), onClick = {onConfirm();onDismiss()}
                ) {
                    Text(text = "Confirm")
                }
            }
        }
    }

}