package com.mjdev.musicplayer.domain.repository

import android.content.IntentSender
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.data.local.relations.MusicWithPlaylists
import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicError
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder
import com.mjdev.musicplayer.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface MainRepository {


    fun getAlbumSortedBy(sortOrder: AlbumSortOrder = AlbumSortOrder.None) : Flow<List<List<MusicItem>>>
    fun getArtistSortedBy(sortOrder: ArtistSortOrder = ArtistSortOrder.None) : Flow<List<List<MusicItem>>>
    fun getAllMusicSortedBy(sortOrder: MusicSortOrder = MusicSortOrder.None): Flow<List<MusicItem>>
    fun getAllMusicsById(ids : List<Long>) : Flow<List<MusicItem>>
    fun getMusicById(id : Long) : Flow<MusicItem>
    suspend fun suspendGetMusicById(id: Long): MusicItem
    suspend fun suspendGetMusicsWithPlayListId(playlistName: String): List<PlayListWithMusics>


    suspend fun cacheMusics(): Result<Unit, MusicError>

    suspend fun upsertMusics(musics: List<MusicItem>)


     fun getMusicByName(name: String): Flow<List<MusicItem>>

     suspend fun deleteMusicFromDevice(musicItems: MusicItem) : IntentSender?
     suspend fun deleteMusicFromDatabase(musicItems: MusicItem)
    suspend fun upsertMusic(musicItem: MusicItem)

    fun getMusicsWithPlayListIdSortedBy(
        playlistName: String,
        sortOrder: MusicSortOrder = MusicSortOrder.None
    ): Flow<List<PlayListWithMusics>>


    fun getAllPlayListsSortedBy(sortOrder: PlayListSortOrder = PlayListSortOrder.None): Flow<List<PlayList>>


    suspend fun upsertPlayList(playList: PlayList)


    suspend fun getPlayListByName(name: String): PlayList


    suspend fun deletePlayList(playList: PlayList)


    fun getPlayListsWithMusicIdSortedBy(
        musicId: Long,
        sortOrder: PlayListSortOrder = PlayListSortOrder.None
    ): Flow<List<MusicWithPlaylists>>

    suspend fun deletePlayListMusicCrossRef(crossRef: MusicPlayListCrossRef)


    suspend fun upsertPlayListMusicCrossRef(crossRef: MusicPlayListCrossRef)


    suspend fun upsertOption(option: Option)

    fun getOption(): Flow<Option?>


    suspend fun upsertInitialMusicItems(initialMusicItems: InitialMusicItems)



    suspend fun getInitialMusicItems() : InitialMusicItems?
}