package com.mjdev.musicplayer.domain.repository

import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {

    suspend fun insertGradient(gradient: Gradient)
    fun readGradient() : Flow<Int?>

    suspend fun insertImageTheme(imageTheme: ImageTheme)
    fun readImageTheme() : Flow<Int?>
}