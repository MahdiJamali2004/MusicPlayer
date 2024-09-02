package com.mjdev.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicItem(
    @PrimaryKey(autoGenerate = false)
    val musicId : Long =0,
    val displayName : String = "",
    val uri : String = "",
    val imgUri : String? = null,
    val artist : String = "",
    val albumArtist : String = "",
    val album : String = "",
    val duration : Long = 0, // milliSeconds
    val size : Long = 0, //bytes
    val dataAdded : Long = 0, // timeStamp
    val year: Long = 0
)



