package com.mjdev.musicplayer.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.data.local.relations.MusicWithPlaylists
import com.mjdev.musicplayer.domain.model.PlayList
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDao {

    @Query("SELECT * FROM PlayList")
    fun getAllPlayLists() : Flow<List<PlayList>>

    @Upsert
    suspend fun upsertPlayList(playList: PlayList)

    @Query("SELECT * FROM playlist WHERE playlistName LIKE :name || '%'")
    suspend fun getPlayListByName(name : String) : PlayList

    @Delete
    suspend fun deletePlayList(playList: PlayList)

    @Transaction
    @Query("SELECT * FROM MusicItem WHERE musicId =:musicId")
    fun getPlayListsWithMusicId(musicId : Long): Flow<List<MusicWithPlaylists>>

    @Upsert
    suspend fun upsertPlayListMusicCrossRef(crossRef : MusicPlayListCrossRef)

    @Delete
    suspend fun deletePlayListMusicCrossRef(crossRef: MusicPlayListCrossRef)

}