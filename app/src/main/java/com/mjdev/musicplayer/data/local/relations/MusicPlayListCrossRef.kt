package com.mjdev.musicplayer.data.local.relations

import androidx.room.Entity

@Entity(primaryKeys = ["musicId" , "playlistName"])
data class MusicPlayListCrossRef(
    val musicId : Long,
    val playlistName : String
)
