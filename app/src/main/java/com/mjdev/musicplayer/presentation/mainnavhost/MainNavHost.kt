@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)

package com.mjdev.musicplayer.presentation.mainnavhost

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.presentation.albumDetailScreen.component.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.albumSrcreen.component.AlbumsScreen
import com.mjdev.musicplayer.presentation.artistDetailScreen.component.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.artistScreen.component.ArtistsScreen
import com.mjdev.musicplayer.presentation.components.CustomTopAppBar
import com.mjdev.musicplayer.presentation.components.MusicBottomBar
import com.mjdev.musicplayer.presentation.components.OptionDropDownMenu
import com.mjdev.musicplayer.presentation.playerScreen.component.HorizontalPlayerScreen
import com.mjdev.musicplayer.presentation.playerScreen.component.PlayerScreen
import com.mjdev.musicplayer.presentation.playingQueueScreen.component.PlayingQueueScreen
import com.mjdev.musicplayer.presentation.playlistDetailScreen.component.PlaylistDetailScreen
import com.mjdev.musicplayer.presentation.playlistScreen.component.PlaylistsScreen
import com.mjdev.musicplayer.presentation.searchScreen.component.SearchScreen
import com.mjdev.musicplayer.presentation.songScreen.component.SongScreen
import com.mjdev.musicplayer.presentation.splashScreen.SplashScreen
import com.mjdev.musicplayer.presentation.themeScreen.component.ThemeScreen
import com.mjdev.musicplayer.presentation.util.AlbumDetailScreen
import com.mjdev.musicplayer.presentation.util.AlbumsScreen
import com.mjdev.musicplayer.presentation.util.ArtistDetailScreen
import com.mjdev.musicplayer.presentation.util.ArtistsScreen
import com.mjdev.musicplayer.presentation.util.NavigationItem
import com.mjdev.musicplayer.presentation.util.PlayerScreen
import com.mjdev.musicplayer.presentation.util.PlayingQueueScreen
import com.mjdev.musicplayer.presentation.util.PlaylistDetailScreen
import com.mjdev.musicplayer.presentation.util.PlaylistsScreen
import com.mjdev.musicplayer.presentation.util.SearchScreen
import com.mjdev.musicplayer.presentation.util.SongsScreen
import com.mjdev.musicplayer.presentation.util.SplashScreen
import com.mjdev.musicplayer.presentation.util.ThemeScreen
import com.mjdev.musicplayer.presentation.util.toBrush
import com.mjdev.musicplayer.presentation.util.toMediaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    viewModel: MainNavHostViewModel = hiltViewModel(),
    settedMusics: List<MediaItem>,
    mediaController: MediaController,
) {

    val navController = rememberNavController()
    val states by viewModel.states.collectAsState()
    val currentBackStackState by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackState?.destination?.route ?: "SplashScreen"
        }
    }
    var bottomBarVisibility by remember { mutableStateOf(false) }
    var topBarVisibility by remember { mutableStateOf(false) }
    var navigateBackVisibility by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var optionDropDownState by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        OptionDropDownMenu(
            isExpanded = optionDropDownState,
            gridNum = states.option.gridNum,
            imageStyle = states.option.imgCornerShape,
            isShuffleEnable = !currentRoute.contains("PlayingQueueScreen"),
            onDismissRequest = { optionDropDownState = false },
            onShuffle = {
                mediaController.shuffleModeEnabled = true
                mediaController.setMediaItems(states.musics.toMediaItem())
            },
            onGridSizeChange = { viewModel.events(MainNavHostEvents.GridNumChanged(it)) },
            onImageStyleChange = { viewModel.events(MainNavHostEvents.ImgCornerShapeChanged(it)) }
        )
    }



    Scaffold(
        modifier = modifier,
        topBar = {
            topBarVisibility = when {
                currentRoute.contains("PlayerScreen") -> false
                currentRoute.contains("SplashScreen") -> false
                currentRoute.contains("SearchScreen") -> false
                currentRoute.contains("ThemeScreen") -> false
                else -> true
            }
            navigateBackVisibility = when {
                currentRoute.contains("AlbumDetailScreen") -> true
                currentRoute.contains("ArtistDetailScreen") -> true
                currentRoute.contains("PlaylistDetailScreen") -> true
                currentRoute.contains("PlayingQueueScreen") -> true
                else -> false
            }
            if (topBarVisibility) {
                CustomTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onSearchClick = { navController.navigate(SearchScreen) },
                    navController = navController,
                    navigationIconEnable = navigateBackVisibility,
                    onOptionClick = { optionDropDownState = true },
                    onThemeScreen = { navController.navigate(ThemeScreen) })

            }


        },
        bottomBar = {
            bottomBarVisibility = when {
                currentRoute.contains("PlayerScreen") -> false
                currentRoute.contains("SplashScreen") -> false
                currentRoute.contains("SearchScreen") -> false
                currentRoute.contains("ThemeScreen") -> false
                else -> true
            }

            if (bottomBarVisibility && settedMusics.isNotEmpty())
                Column {

                    MusicBottomBar(
                        mediaController = mediaController,
                        onClick = {
                            navController.navigate(
                                PlayerScreen(
                                    mediaController.currentMediaItem?.mediaId?.toLong() ?: 0
                                )
                            )
                        }
                    )
                    NavigationBar(containerColor = Color.White.copy(alpha = 0.15f)) {
                        navigationItems().forEach { navItem ->
                            NavigationBarItem(selected = navItem.stringRoute == currentBackStackState?.destination?.route?.substringAfterLast(
                                '.'
                            ), colors = NavigationBarItemColors(
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                selectedIndicatorColor = MaterialTheme.colorScheme.surface,
                                unselectedIconColor = Color.White.copy(0.5f),
                                unselectedTextColor = Color.White.copy(0.5f),
                                disabledIconColor = Color.Gray,
                                disabledTextColor = Color.Gray
                            ),
                                onClick = {
                                    navController.popBackStack()
                                    navController.navigate(navItem.route)
                                }, label = { Text(text = navItem.title, maxLines = 1) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = navItem.icon),
                                        contentDescription = "${navItem.title} screen"
                                    )
                                })
                        }
                    }
                }


        },


        ) {


        val startDestination: Any = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SongsScreen
        } else {
            SplashScreen
        }
        if (states.imageTheme != ImageTheme.None) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(states.imageTheme.drawable)
                        .build(),
                    filterQuality = FilterQuality.None,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
//                Image(
//                    modifier = Modifier.fillMaxSize(),
//                    painter = painterResource(id = states.imageTheme.drawable),
//                    contentDescription = null,
//                    contentScale = ContentScale.FillBounds
//                )

            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = states.gradient.toBrush())
        ) {
            NavHost(
                modifier = Modifier
                    .padding(it),
                navController = navController,
                startDestination = startDestination
            ) {

                composable<PlayerScreen> {
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        HorizontalPlayerScreen(
                            mediaController = mediaController,
                            navController = navController
                        )
                    } else {
                        PlayerScreen(
                            mediaController = mediaController,
                            navController = navController
                        )

                    }
                }
                composable<SongsScreen> {

                    SongScreen(navController = navController, mediaController = mediaController)
                }
                composable<AlbumsScreen> {

                    AlbumsScreen(
                        navController = navController,
                        mediaController = mediaController
                    )
                    Log.v("screenLog", "artist")
                }
                composable<ArtistsScreen> {
                    ArtistsScreen(
                        navController = navController,
                        mediaController = mediaController
                    )
                }
                composable<PlaylistsScreen> {
                    PlaylistsScreen(
                        navController = navController,
                        mediaController = mediaController
                    )
                }

                composable<SplashScreen> {
                    SplashScreen(navController = navController)
                }
                composable<SearchScreen> {

                    SearchScreen(
                        mediaController = mediaController,
                        navController = navController
                    )
                }

                composable<AlbumDetailScreen> {
                    AlbumDetailScreen(
                        mediaController = mediaController,
                        navController = navController
                    )
                }
                composable<ArtistDetailScreen> {
                    ArtistDetailScreen(
                        mediaController = mediaController,
                        navController = navController
                    )
                }
                composable<PlaylistDetailScreen> {
                    PlaylistDetailScreen(
                        mediaController = mediaController,
                        navController = navController
                    )
                }

                composable<ThemeScreen> {
                    ThemeScreen(
                        navController = navController
                    )
                }

                composable<PlayingQueueScreen> {
                    PlayingQueueScreen(
                        mediaController = mediaController,
                        navController = navController
                    )
                }
            }
        }
    }


}


private fun navigationItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(SongsScreen, "SongsScreen", "Songs", R.drawable.ic_music_field),
        NavigationItem(
            ArtistsScreen,
            "ArtistsScreen",
            "Artists",
            R.drawable.ic_artist
        ),
        NavigationItem(AlbumsScreen, "AlbumsScreen", "Albums", R.drawable.ic_album_field),
        NavigationItem(
            PlaylistsScreen,
            "PlaylistsScreen",
            "Playlists",
            R.drawable.ic_music_queue
        ),
    )
}
