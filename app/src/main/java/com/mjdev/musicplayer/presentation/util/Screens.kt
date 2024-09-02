package com.mjdev.musicplayer.presentation.util

import kotlinx.serialization.Serializable


@Serializable
object SongsScreen

@Serializable
object AlbumsScreen

@Serializable
object ArtistsScreen


@Serializable
object SplashScreen

@Serializable
object PlaylistsScreen

@Serializable
object SearchScreen

@Serializable
object ThemeScreen

@Serializable
object PlayingQueueScreen

@Serializable
data class PlayerScreen(val musicItemId : Long)

@Serializable
data class AlbumDetailScreen(val musicItemId : Long)

@Serializable
data class ArtistDetailScreen(val musicItemId : Long)

@Serializable
data class PlaylistDetailScreen(val playlistName : String)

data class NavigationItem (
    val route : Any,
    val stringRoute : String,
    val title : String,
    val icon : Int
)

