package com.mjdev.musicplayer.presentation.mainnavhost

import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.model.ImgCornerShape

sealed class MainNavHostEvents {
    data class ImgCornerShapeChanged(val imgCornerShape: ImgCornerShape) : MainNavHostEvents()
    data class GridNumChanged(val gridNum: GridNum) : MainNavHostEvents()
}