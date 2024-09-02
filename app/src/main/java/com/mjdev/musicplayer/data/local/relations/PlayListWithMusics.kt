package com.mjdev.musicplayer.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction

import androidx.room.Relation
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList

data class PlayListWithMusics(
    @Embedded val playlist: PlayList,
    @Relation(
        parentColumn = "playlistName",
        entityColumn = "musicId",
        associateBy = Junction(MusicPlayListCrossRef::class)
    ) val musics : List<MusicItem>
)
