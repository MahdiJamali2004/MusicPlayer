package com.mjdev.musicplayer.domain.util


enum class MusicSortOrder {
    None,
    Title,
    DateAdded,
    Artist,
    Album,
    AlbumArtist,
    Size,
    Year,
    Duration;
    companion object{
        fun valueOf(ordinal : Int) : MusicSortOrder? {
            return entries.firstOrNull { it.ordinal == ordinal }
        }
    }
}

enum class PlayListSortOrder {
    None,
    Title,
    DateAdded;
    companion object{
        fun valueOf(ordinal : Int) : PlayListSortOrder? {
            return PlayListSortOrder.entries.firstOrNull { it.ordinal == ordinal }
        }
    }
}

enum class AlbumSortOrder {
    None,
    Title,
    AlbumArtist,
    Year,
    DateAdded,
    SongCount;
    companion object{
        fun valueOf(ordinal : Int) : AlbumSortOrder? {
            return AlbumSortOrder.entries.firstOrNull { it.ordinal == ordinal }
        }
    }
}
enum class ArtistSortOrder {
    None,
    Title,
    Year,
    DateAdded,
    SongCount;
    companion object{
        fun valueOf(ordinal : Int) : ArtistSortOrder? {
            return ArtistSortOrder.entries.firstOrNull { it.ordinal == ordinal }
        }
    }
}
