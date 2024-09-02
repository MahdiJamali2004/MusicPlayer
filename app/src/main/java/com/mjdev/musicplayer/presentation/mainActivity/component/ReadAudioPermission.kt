package com.mjdev.musicplayer.presentation.mainActivity.component

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale

@Composable
fun ReadAudioPermission(
    modifier: Modifier = Modifier,
    isPermanentlyDeclined: Boolean,
    onDismissRequest : () -> Unit,
    onConfirmClick : () -> Unit,
    onOpenSetting: () -> Unit
) {

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onOpenSetting()
                            onDismissRequest()
                        } else {
                            onConfirmClick()
                            onDismissRequest()
                        }

                    },
                contentAlignment = Alignment.Center
            ) {

                    Text(
                        text = "Confirm",
                        fontWeight = FontWeight.SemiBold,
                    )


            }
        },
        title = {
            Text(
                text = "ReadAudio Permission",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        },
        text = {
            val text = if (isPermanentlyDeclined) {
                "it seems like you permanently declined ReadAudio permission go to setting and set permission."
            } else {
                "ReadAudio permission required to read sounds from storage."
            }
            Text(
                text = text,
                fontWeight = FontWeight.Normal,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Color.Gray
            )
        }, icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = "Warning"
            )
        }
    )

}

