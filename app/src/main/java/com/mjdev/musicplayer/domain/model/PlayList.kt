package com.mjdev.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayList(
    @PrimaryKey(autoGenerate = false)
    val playlistName : String,
    val dateAdded : Long,
)
