package com.mjdev.musicplayer.presentation.themeScreen

import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme

data class ThemeStates(
    val gradient: Gradient = Gradient.None,
    val imageTheme: ImageTheme = ImageTheme.None,
    val isGradient : Boolean = false
)
