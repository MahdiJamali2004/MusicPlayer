package com.mjdev.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mjdev.musicplayer.data.local.converters.Converters

@Entity
data class InitialMusicItems(
    @PrimaryKey(autoGenerate = false)
    val id : Int = 1,
    @TypeConverters(Converters::class)
    val musicItemId : List<Long> = emptyList(),
    val startMusicIndex : Int  = 0,
    val startMusicPosition : Long = 0
)