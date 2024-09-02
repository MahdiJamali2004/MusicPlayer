package com.mjdev.musicplayer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mjdev.musicplayer.presentation.ui.theme.spacing

@Composable
fun SortBottomSheet(
    modifier: Modifier = Modifier,
    sortEntries: List<String>,
    selectedItemIndex: Int,
    onDismiss : () -> Unit,
    onClick: (index: Int) -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)){
            Text(text = "sort order" , fontSize = MaterialTheme.typography.titleMedium.fontSize)
        }
        HorizontalDivider()
        sortEntries.forEachIndexed { index, title ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(index);onDismiss() }
                .padding(MaterialTheme.spacing.medium)
               ) {
                if (selectedItemIndex == index) {
                    Icon(imageVector = Icons.Rounded.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Text(
                    text = title, fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )

            }
        }

    }

}