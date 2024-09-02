package com.mjdev.musicplayer.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.mjdev.musicplayer.domain.model.ImgCornerShape
import com.mjdev.musicplayer.presentation.ui.theme.spacing

@Composable
fun ImgCornerShape.value(): Dp {
    return when (this) {
        ImgCornerShape.Sharp -> MaterialTheme.spacing.default
        ImgCornerShape.Round -> MaterialTheme.spacing.extraSmall
        ImgCornerShape.Circle -> MaterialTheme.spacing.circle
    }

}