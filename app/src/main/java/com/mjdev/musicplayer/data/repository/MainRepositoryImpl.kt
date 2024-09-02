package com.mjdev.musicplayer.data.repository

import android.content.IntentSender
import android.util.Log
import com.mjdev.musicplayer.data.local.DataBase
import com.mjdev.musicplayer.data.local.relations.MusicPlayListCrossRef
import com.mjdev.musicplayer.data.local.relations.MusicWithPlaylists
import com.mjdev.musicplayer.data.local.relations.PlayListWithMusics
import com.mjdev.musicplayer.data.music.DeleteMusic
import com.mjdev.musicplayer.data.music.GetMusics
import com.mjdev.musicplayer.domain.model.InitialMusicItems
import com.mjdev.musicplayer.domain.model.MusicItem
import com.mjdev.musicplayer.domain.model.Option
import com.mjdev.musicplayer.domain.model.PlayList
import com.mjdev.musicplayer.domain.repository.MainRepository
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicError
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder
import com.mjdev.musicplayer.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val dataBase: DataBase,
    private val getMusics: GetMusics,
    private val deleteMusicDevice: DeleteMusic
) : MainRepository {
    override suspend fun deletePlayListMusicCrossRef(crossRef: MusicPlayListCrossRef) {
        dataBase.playListDao.deletePlayListMusicCrossRef(crossRef)
    }

    override fun getAlbumSortedBy(sortOrder: AlbumSortOrder): Flow<List<List<MusicItem>>> {
        return dataBase.musicDao.getAllMusic().map {list ->
            when(sortOrder){
                AlbumSortOrder.None -> list
                AlbumSortOrder.Title -> list.sortedBy { it.album }
                AlbumSortOrder.AlbumArtist -> {list.sortedBy { it.albumArtist }}
                AlbumSortOrder.Year -> {list.sortedByDescending { it.year }}
                AlbumSortOrder.DateAdded -> {list.sortedByDescending { it.dataAdded }}
                AlbumSortOrder.SongCount -> list//didn't considered here
            }
        }.map {
           val albumLists = it.groupBy { musicItem ->musicItem.album }.values.toList()
            if(sortOrder == AlbumSortOrder.SongCount){
                albumLists.sortedByDescending { albums ->albums.size }
            }else{
                albumLists
            }
        }
    }
    override fun getAllMusicsById(ids: List<Long>): Flow<List<MusicItem>> {
        return dataBase.musicDao.getAllMusicsById(ids)
    }

    override fun getMusicById(id: Long): Flow<MusicItem> {
        return dataBase.musicDao.getMusicById(id)
    }

    override suspend fun suspendGetMusicById(id: Long): MusicItem {
        return dataBase.musicDao.suspendGetMusicById(id)
    }

    override fun getArtistSortedBy(sortOrder: ArtistSortOrder): Flow<List<List<MusicItem>>> {
        return dataBase.musicDao.getAllMusic().map {list ->
            when(sortOrder){
                ArtistSortOrder.None -> list
                ArtistSortOrder.Title -> list.sortedBy { it.album }
                ArtistSortOrder.Year -> {list.sortedByDescending { it.year }}
                ArtistSortOrder.DateAdded -> {list.sortedByDescending { it.dataAdded }}
                ArtistSortOrder.SongCount -> list//didn't considered here

            }
        }.map { sortedList ->
            val artistLists = sortedList.groupBy { it.artist }.values.toList()
            if(sortOrder == ArtistSortOrder.SongCount){
                artistLists.sortedByDescending { sortedList.size }
            }else{
                artistLists
            }
        }
    }

    override fun getAllMusicSortedBy(sortOrder: MusicSortOrder): Flow<List<MusicItem>> {
        return dataBase.musicDao.getAllMusic()
            .map { musicList ->
                when (sortOrder) {
                    MusicSortOrder.None -> {
                        musicList.sortedBy { it.musicId }
                    }

                    MusicSortOrder.Title -> {
                        musicList.sortedBy { it.displayName.lowercase() }
                    }

                    MusicSortOrder.DateAdded -> {
                        musicList.sortedByDescending { it.dataAdded }
                    }

                    MusicSortOrder.Artist -> {
                        musicList.sortedBy { it.artist.lowercase() }
                    }

                    MusicSortOrder.Album -> {
                        musicList.sortedBy { it.album.lowercase() }
                    }

                    MusicSortOrder.AlbumArtist -> {
                        musicList.sortedBy { it.albumArtist.lowercase() }
                    }


                    MusicSortOrder.Size -> {
                        musicList.sortedBy { it.size }
                    }

                    MusicSortOrder.Year -> {
                        musicList.sortedBy { it.year }
                    }

                    MusicSortOrder.Duration -> {
                        musicList.sortedBy { it.duration }
                    }

                }
            }
    }

    override suspend fun cacheMusics(): Result<Unit, MusicError> {
        return when (val result = getMusics()) {
            is Result.Error -> {
                Result.Error(result.error)
            }

            is Result.Success -> {
                val currentMusics = dataBase.musicDao.getAllMusic().first()
                val deletedMusics = currentMusics.filter { it !in result.data }
                dataBase.musicDao.deleteMusics(deletedMusics)
                dataBase.musicDao.upsertMusics(result.data)
                Result.Success(Unit)
            }
        }
    }

    override suspend fun upsertMusics(musics: List<MusicItem>) {
        dataBase.musicDao.upsertMusics(musics)
    }

    override fun getMusicByName(name: String): Flow<List<MusicItem>> {
        return dataBase.musicDao.getMusicByName(name)
    }

    override suspend fun deleteMusicFromDevice(musicItems: MusicItem) : IntentSender? {
        return deleteMusicDevice(musicItems)

    }

    override suspend fun deleteMusicFromDatabase(musicItems: MusicItem) {
        dataBase.musicDao.deleteMusic(musicItems)
    }

    override suspend fun upsertMusic(musicItem: MusicItem) {
        dataBase.musicDao.upsertMusic(musicItem)
    }

    override fun getMusicsWithPlayListIdSortedBy(
        playlistName: String,
        sortOrder: MusicSortOrder
    ): Flow<List<PlayListWithMusics>> {
        return dataBase.musicDao.getMusicsWithPlayListId(playlistName)
            .map { list ->
                list.map { musicsWithPlayList ->
                    val sortedMusics = when (sortOrder) {
                        MusicSortOrder.None -> {
                            musicsWithPlayList.musics.sortedBy { it.musicId }
                        }

                        MusicSortOrder.Title -> {
                            musicsWithPlayList.musics.sortedBy { it.displayName.lowercase() }
                        }

                        MusicSortOrder.DateAdded -> {
                            musicsWithPlayList.musics.sortedByDescending { it.dataAdded }
                        }

                        MusicSortOrder.Artist -> {
                            musicsWithPlayList.musics.sortedBy { it.artist.lowercase() }
                        }

                        MusicSortOrder.Album -> {
                            musicsWithPlayList.musics.sortedBy { it.album.lowercase() }
                        }

                        MusicSortOrder.AlbumArtist -> {
                            musicsWithPlayList.musics.sortedBy { it.albumArtist.lowercase() }
                        }

                        MusicSortOrder.Size -> {
                            musicsWithPlayList.musics.sortedByDescending { it.size }
                        }

                        MusicSortOrder.Year -> {
                            musicsWithPlayList.musics.sortedByDescending { it.year }
                        }

                        MusicSortOrder.Duration -> {
                            musicsWithPlayList.musics.sortedByDescending { it.duration }
                        }


                    }
                    PlayListWithMusics(musicsWithPlayList.playlist, sortedMusics)
                }
            }
    }

    override fun getAllPlayListsSortedBy(sortOrder: PlayListSortOrder): Flow<List<PlayList>> {
        return dataBase.playListDao.getAllPlayLists()
            .map { playLists ->
                when (sortOrder) {
                    PlayListSortOrder.None -> {
                        playLists
                    }

                    PlayListSortOrder.Title -> {
                        playLists.sortedBy { it.playlistName.lowercase() }
                    }

                    PlayListSortOrder.DateAdded -> {
                        playLists.sortedByDescending { it.dateAdded }
                    }


                }
            }
    }

    override suspend fun suspendGetMusicsWithPlayListId(
        playlistName: String
    ): List<PlayListWithMusics> {
       return dataBase.musicDao.suspendGetMusicsWithPlayListId(playlistName)
    }

    override suspend fun upsertOption(option: Option) {
        dataBase.optionDao.upsertOption(option)
    }

    override fun getOption(): Flow<Option> {
        return dataBase.optionDao.getOption()
    }

    override suspend fun upsertInitialMusicItems(initialMusicItems: InitialMusicItems) {
        dataBase.initialMusicItemsDao.upsertInitialMusicItems(initialMusicItems)
    }

    override suspend fun getInitialMusicItems(): InitialMusicItems {
       return dataBase.initialMusicItemsDao.getInitialMusicItems()
    }



    override suspend fun upsertPlayList(playList: PlayList) {
        dataBase.playListDao.upsertPlayList(playList)
    }

    override suspend fun getPlayListByName(name: String): PlayList {
        return dataBase.playListDao.getPlayListByName(name)
    }

    override suspend fun deletePlayList(playList: PlayList) {
        dataBase.playListDao.deletePlayList(playList)
    }

    override fun getPlayListsWithMusicIdSortedBy(
        musicId: Long,
        sortOrder: PlayListSortOrder
    ): Flow<List<MusicWithPlaylists>> {
        return dataBase.playListDao.getPlayListsWithMusicId(musicId)
            .map { list ->
                list.map { musicWithPlayLists ->
                    val sortedPlayList = when (sortOrder) {
                        PlayListSortOrder.None -> {
                            musicWithPlayLists.playLists
                        }

                        PlayListSortOrder.Title -> {
                            musicWithPlayLists.playLists.sortedBy { it.playlistName.lowercase() }
                        }

                        PlayListSortOrder.DateAdded -> {
                            musicWithPlayLists.playLists.sortedByDescending { it.dateAdded }
                        }

                    }
                    MusicWithPlaylists(music = musicWithPlayLists.music, playLists = sortedPlayList)
                }

            }
    }

    override suspend fun upsertPlayListMusicCrossRef(crossRef: MusicPlayListCrossRef) {
        dataBase.playListDao.upsertPlayListMusicCrossRef(crossRef)
    }
}
