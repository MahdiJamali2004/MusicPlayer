package com.mjdev.musicplayer.presentation.themeScreen.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.YellowContainer
import com.mjdev.musicplayer.R
import com.mjdev.musicplayer.domain.model.Gradient
import com.mjdev.musicplayer.domain.model.ImageTheme
import com.mjdev.musicplayer.presentation.themeScreen.ThemeEvents
import com.mjdev.musicplayer.presentation.themeScreen.ThemeScreenViewModel
import com.mjdev.musicplayer.presentation.ui.theme.spacing
import com.mjdev.musicplayer.presentation.util.toBrush
import kotlinx.coroutines.launch

@Composable
fun ThemeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ThemeScreenViewModel = hiltViewModel()
) {
    val states by viewModel.states.collectAsState()
    val scope = rememberCoroutineScope()
    val imageFadeAnim = remember {
        Animatable(0f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(states.gradient.toBrush())
    ) {
        if (states.imageTheme != ImageTheme.None) {
            Image(
                painter = painterResource(id = states.imageTheme.drawable),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))

            )

        }
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrowback),
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
                Text(
                    text = stringResource(R.string.choose_theme),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.events(ThemeEvents.IsGradientChange(false)) }) {
                        Icon(
                            imageVector = Icons.Rounded.Image,
                            contentDescription = stringResource(R.string.images),
                            tint = if (!states.isGradient) YellowContainer else Color.White.copy(
                                alpha = 0.5f
                            )
                        )
                    }

                    IconButton(onClick = { viewModel.events(ThemeEvents.IsGradientChange(true)) }) {
                        Icon(
                            imageVector = Icons.Rounded.Gradient,
                            contentDescription = stringResource(R.string.images),
                            tint = if (states.isGradient) YellowContainer else Color.White.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                }
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                verticalItemSpacing = MaterialTheme.spacing.large,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large),
                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
            ) {

                if (states.isGradient) {
                    items(Gradient.entries) {
                        if (it == Gradient.None) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp, 200.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        RoundedCornerShape(MaterialTheme.spacing.medium)
                                    )
                                    .clickable {
                                        viewModel.events(ThemeEvents.GradientChange(it))
                                    }
                                    .padding(MaterialTheme.spacing.small),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Default color",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(100.dp, 200.dp)
                                    .border(
                                        BorderStroke(
                                            MaterialTheme.spacing.superExtraSmall,
                                            MaterialTheme.colorScheme.surface
                                        ),
                                        RoundedCornerShape(MaterialTheme.spacing.medium)
                                    )
                                    .background(
                                        brush = it.toBrush(),
                                        RoundedCornerShape(MaterialTheme.spacing.medium)
                                    )
                                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                                    .clickable {
                                        viewModel.events(ThemeEvents.GradientChange(it))
                                    }
                            )

                        }
                    }

                } else {
                    itemsIndexed(ImageTheme.entries) { index, it ->
                        if (it == ImageTheme.None) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp, 200.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        RoundedCornerShape(MaterialTheme.spacing.medium)
                                    )
//                                    .border(
//                                        BorderStroke(
//                                            MaterialTheme.spacing.superExtraSmall,
//                                            MaterialTheme.colorScheme.onSurface
//                                        ),
//                                        RoundedCornerShape(MaterialTheme.spacing.medium)
//                                    )
                                    .padding(MaterialTheme.spacing.small)
                                    .clickable {
                                        viewModel.events(ThemeEvents.ImageThemeChange(it))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Default color",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                                )
                            }
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it.drawable)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "background",
                                alpha = imageFadeAnim.value,
                                onSuccess = {
                                    scope.launch {
                                        imageFadeAnim.animateTo(1f)
                                    }
                                },
                                contentScale = ContentScale.FillBounds,
                                filterQuality = FilterQuality.None,
                                modifier = Modifier
                                    .size(100.dp, 200.dp)
                                    .border(
                                        BorderStroke(
                                            MaterialTheme.spacing.superExtraSmall,
                                            MaterialTheme.colorScheme.surface
                                        ),
                                        RoundedCornerShape(MaterialTheme.spacing.medium)
                                    )
                                    .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                                    .clickable {
                                        viewModel.events(ThemeEvents.ImageThemeChange(it))
                                    }
                            )

                        }
                    }
                }

            }
        }

    }
}

