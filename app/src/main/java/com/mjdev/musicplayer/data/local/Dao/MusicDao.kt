package com.mjdev.musicplayer.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.MusicItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM MusicItem")
    fun getAllMusic(): Flow<List<MusicItem>>

//    @Query("SELECT * FROM MusicItem")
//    suspend fun suspendGetAllMusic(): List<MusicItem>

    @Query("SELECT * FROM MusicItem WHERE musicId IN (:ids)")
    fun getAllMusicsById(ids: List<Long>): Flow<List<MusicItem>>

    @Query("SELECT * FROM MusicItem WHERE musicId = :id")
    fun getMusicById(id: Long): Flow<MusicItem>

    @Query("SELECT * FROM MusicItem WHERE musicId = :id")
    suspend fun suspendGetMusicById(id: Long): MusicItem

    @Upsert
    suspend fun upsertMusics(musics: List<MusicItem>)

    @Upsert
    suspend fun upsertMusic(musicItem: MusicItem)

    @Query("SELECT * FROM MusicItem WHERE displayName LIKE :name || '%'")
    fun getMusicByName(name: String): Flow<List<MusicItem>>

    @Delete
    suspend fun deleteMusic(musicItem: MusicItem)

    @Delete
    suspend fun deleteMusics(musicItem: List<MusicItem>)

    @Transaction
    @Query("SELECT * FROM PlayList WHERE playlistName =:playlistName")
    fun getMusicsWithPlayListId(playlistName: String): Flow<List<PlayListWithMusics>>

    @Transaction
    @Query("SELECT * FROM PlayList WHERE playlistName =:playlistName")
    suspend fun suspendGetMusicsWithPlayListId(playlistName: String): List<PlayListWithMusics>


}