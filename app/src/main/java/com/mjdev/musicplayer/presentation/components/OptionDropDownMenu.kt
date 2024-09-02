package com.mjdev.musicplayer.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.presentation.ui.theme.spacing

@Composable
fun OptionDropDownMenu(
    isExpanded: Boolean,
    gridNum: GridNum,
    imageStyle: ImgCornerShape,
    onDismissRequest: () -> Unit,
    isShuffleEnable : Boolean,
    onShuffle: () -> Unit,
    onGridSizeChange: (GridNum) -> Unit,
    onImageStyleChange: (ImgCornerShape) -> Unit
) {
    var gridSizeState by remember { mutableStateOf(false) }
    var imageStyleState by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    DropdownMenu(
        modifier = Modifier.width(160.dp),
        offset = DpOffset(screenWidthDp,0.dp),
        expanded = isExpanded,
        onDismissRequest = {onDismissRequest() }) {
        if (isShuffleEnable){
            DropdownMenuItem(text = {
                Text(text = stringResource(R.string.shuffle_all))
            }, onClick = {
                onDismissRequest()
                onShuffle()
            }, contentPadding = PaddingValues(MaterialTheme.spacing.medium))
        }
        DropdownMenuItem(text = {
            Text(text = stringResource(R.string.grid_size))
        }, trailingIcon = {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
        }, onClick = { gridSizeState = true })
        DropdownMenuItem(text = {
            Text(text = stringResource(R.string.image_style))
        }, trailingIcon = {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowRight, contentDescription = null)
        }, onClick = { imageStyleState = true })
    }
    DropdownMenu(
        offset =  DpOffset(screenWidthDp,0.dp),
        modifier = Modifier.width(160.dp),
        expanded = gridSizeState,
        onDismissRequest = { gridSizeState = false }) {
        DropdownMenuItem(
            modifier = Modifier.fillMaxWidth(),
            text = { Text(text = stringResource(R.string.grid_size), color = Color.Gray) },
            enabled = false,
            onClick = {}
        )
        GridNum.entries.forEachIndexed { _, item ->
            DropdownMenuItem(text = { Text(text = "${item.value}") },
                onClick = {
                    gridSizeState = false
                    onDismissRequest()
                    onGridSizeChange(item)
                },
                trailingIcon = {
                    RadioButton(
                        selected = gridNum == item,
                        onClick = {
                            gridSizeState = false; onDismissRequest(); onGridSizeChange(item)
                        })
                })
        }
    }
    DropdownMenu(
        offset =  DpOffset(screenWidthDp,0.dp),
        modifier = Modifier.width(160.dp),
        expanded = imageStyleState,
        onDismissRequest = { imageStyleState = false }) {
        DropdownMenuItem(
            modifier = Modifier.fillMaxWidth(),
            text = { Text(text = stringResource(R.string.image_style), color = Color.Gray) },
            enabled = false,
            onClick = {}
        )
        ImgCornerShape.entries.forEachIndexed { _, item ->
            DropdownMenuItem(text = { Text(text = item.name) },
                onClick = {
                    imageStyleState = false
                    onDismissRequest()
                    onImageStyleChange(item)
                },
                trailingIcon = {
                    RadioButton(
                        selected = imageStyle == item,
                        onClick = {
                            gridSizeState = false; onDismissRequest();onImageStyleChange(
                            item
                        )
                        })
                })
        }
    }

}
