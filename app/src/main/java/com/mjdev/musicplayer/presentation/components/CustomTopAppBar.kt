@file:OptIn(ExperimentalMaterial3Api::class)

package com.mjdev.musicplayer.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.mjdev.musicplayer.R


@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIconEnable: Boolean,
    navController: NavController,
    onSearchClick: () -> Unit,
    onOptionClick: () -> Unit,
    onThemeScreen: () -> Unit
) {


    TopAppBar(colors = TopAppBarColors(
        containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent,
        Color.Gray, MaterialTheme.colorScheme.onSurface, Color.Gray
    ), title = {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    },
        navigationIcon = {
            if (navigationIconEnable) {
                IconButton(onClick = { navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrowback),
                        tint = Color.White.copy(alpha = 0.5f),
                    )
                }

            }

        }, actions = {
            Row {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = Color.White.copy(alpha = 0.5f),
                    )
                }


                IconButton(onClick = { onThemeScreen() }) {
                    Icon(
                        imageVector = Icons.Rounded.ColorLens,
                        contentDescription = stringResource(R.string.theme),
                        tint = Color.White.copy(alpha = 0.5f),
                    )


                }
                IconButton(onClick = { onOptionClick() }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.option),
                        tint = Color.White.copy(alpha = 0.5f),
                    )


                }
            }
        }, modifier = modifier,
        scrollBehavior = scrollBehavior
    )

}

