package com.mjdev.musicplayer.presentation.themeScreen

import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme

sealed class ThemeEvents {
    data class GradientChange(val gradient: Gradient) : ThemeEvents()
    data class ImageThemeChange(val imageTheme: ImageTheme) : ThemeEvents()
    data class IsGradientChange(val isGradient: Boolean) : ThemeEvents()
}