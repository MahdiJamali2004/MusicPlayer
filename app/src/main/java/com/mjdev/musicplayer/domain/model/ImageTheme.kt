package com.mjdev.musicplayer.domain.model

import androidx.annotation.DrawableRes
import com.mjdev.musicplayer.R

enum class ImageTheme( val drawable: Int) {
    None(0),
    B1(R.drawable.b1),
    B2(R.drawable.b2),
    B3(R.drawable.b3),
    B4(R.drawable.b4),
    B5(R.drawable.b5),
    B6(R.drawable.b6),
    B7(R.drawable.b7),
    B8(R.drawable.b8),
    B9(R.drawable.b9),
    B10(R.drawable.b10),
    B11(R.drawable.b11),
    B12(R.drawable.b12),
    B13(R.drawable.b13),
    B14(R.drawable.b14),
    B16(R.drawable.b16),
    B17(R.drawable.b17),
    B19(R.drawable.b19),
    B20(R.drawable.b20),
    B21(R.drawable.b21),
    B22(R.drawable.b22),
    B23(R.drawable.b23),
    B25(R.drawable.b26),
    B26(R.drawable.b27),
    B27(R.drawable.b28);

    companion object {
        fun valueOf(ordinal: Int?): ImageTheme? {
            return if (ordinal == null)
                None
            else {
                ImageTheme.entries.firstOrNull { it.ordinal == ordinal }
            }
        }
    }
}