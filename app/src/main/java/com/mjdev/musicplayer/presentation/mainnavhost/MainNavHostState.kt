package com.mjdev.musicplayer.presentation.mainnavhost

import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option

data class MainNavHostState(
    val musics : List<MusicItem> = emptyList(),
    val option: Option = Option(),
    val gradient: Gradient = Gradient.None,
    val imageTheme: ImageTheme = ImageTheme.None
)
