package com.mjdev.musicplayer.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mjdev.musicplayer.domain.model.GridNum
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder


class Converters {

    @TypeConverter
    fun fromGridNum(gridNum: GridNum) : String {
        return gridNum.name
    }
    @TypeConverter
    fun toGridNum(value : String) : GridNum {
        return GridNum.valueOf(value)
    }


    @TypeConverter
    fun fromMusicSortOrder(musicSortOrder: MusicSortOrder) : String {
        return musicSortOrder.name
    }
    @TypeConverter
    fun toMusicSortOrder(value : String) : MusicSortOrder {
        return  MusicSortOrder.valueOf(value)
    }

    @TypeConverter
    fun fromPlaylistSortOrder(playListSortOrder: PlayListSortOrder) : String {
        return playListSortOrder.name
    }
    @TypeConverter
    fun toPlaylistSortOrder(value : String) : PlayListSortOrder {
        return PlayListSortOrder.valueOf(value)
    }


    @TypeConverter
    fun fromAlbumSortOrder(albumSortOrder: AlbumSortOrder) : String {
        return albumSortOrder.name
    }
    @TypeConverter
    fun toAlbumSortOrder(value : String) : AlbumSortOrder {
        return AlbumSortOrder.valueOf(value)
    }

    @TypeConverter
    fun formArtistSortOrder(artistSortOrder: ArtistSortOrder) : String {
        return artistSortOrder.name
    }
    @TypeConverter
    fun toArtistSortOrder(value : String) : ArtistSortOrder {
        return ArtistSortOrder.valueOf(value)
    }
    @TypeConverter
    fun fromListId(list : List<Long>) :String {
        val gson = Gson()
        return gson.toJson(list)
    }
    @TypeConverter
    fun toListId(value : String) :List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }

}