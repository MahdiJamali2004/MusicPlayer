package com.mjdev.musicplayer.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction

import androidx.room.Relation
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.PlayList

data class MusicWithPlaylists(
    @Embedded val music: MusicItem,
    @Relation(
        parentColumn = "musicId",
        entityColumn = "playlistName",
        associateBy = Junction(MusicPlayListCrossRef::class)
    ) val playLists : List<PlayList>
)
