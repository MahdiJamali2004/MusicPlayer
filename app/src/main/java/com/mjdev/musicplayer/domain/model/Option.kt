package com.mjdev.musicplayer.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mjdev.musicplayer.data.local.converters.Converters
import com.mjdev.musicplayer.domain.util.AlbumSortOrder
import com.mjdev.musicplayer.domain.util.ArtistSortOrder
import com.mjdev.musicplayer.domain.util.MusicSortOrder
import com.mjdev.musicplayer.domain.util.PlayListSortOrder

@Entity
data class Option(
    @PrimaryKey(autoGenerate = false)
    val id : Int = 1,
    @TypeConverters(Converters::class)
    val gridNum : GridNum = GridNum.One,
    @TypeConverters(Converters::class)
    val imgCornerShape : ImgCornerShape = ImgCornerShape.Round,
    @TypeConverters(Converters::class)
    val musicSortOrder : MusicSortOrder = MusicSortOrder.Title,
    @TypeConverters(Converters::class)
    val albumSortOrder : AlbumSortOrder = AlbumSortOrder.Title,
    @TypeConverters(Converters::class)
    val artistSortOrder : ArtistSortOrder = ArtistSortOrder.Title,
    @TypeConverters(Converters::class)
    val playlistSortOrder : PlayListSortOrder = PlayListSortOrder.Title,
)

enum class GridNum(val value : Int){
    One(1),
    Two(2),
    Three(3),
    Four(4);
    companion object{
        fun valueOf(value : Int): GridNum? {
            return entries.firstOrNull { it.value == value }
        }
    }
}
enum class ImgCornerShape{
    Sharp,
    Round,
    Circle
}
