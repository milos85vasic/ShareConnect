/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.ui.components.PlexImageLoader
import com.shareconnect.plexconnect.ui.viewmodels.MediaListViewModel
import com.shareconnect.plexconnect.ui.viewmodels.MediaListViewModelFactory
import com.shareconnect.plexconnect.data.model.PlexMediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListScreen(navController: NavController, serverId: Long, libraryKey: String) {
    val context = LocalContext.current
    val serverRepository = remember { DependencyContainer.plexServerRepository }
    val libraryRepository = remember { DependencyContainer.plexLibraryRepository }
    val mediaRepository = remember { DependencyContainer.plexMediaRepository }

    val viewModel: MediaListViewModel = viewModel(
        factory = MediaListViewModelFactory(
            serverRepository,
            libraryRepository,
            mediaRepository,
            serverId,
            libraryKey
        )
    )

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val server by viewModel.server.collectAsState()
    val library by viewModel.library.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()

    val gridState = rememberLazyGridState()

    // Load more items when reaching the end
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= mediaItems.size - 10) {
                    viewModel.loadMoreItems()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(library?.title ?: "Media")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadMediaItems(refresh = true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && mediaItems.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorScreen(
                        error = error!!,
                        onRetry = { viewModel.loadMediaItems(refresh = true) }
                    )
                }
                mediaItems.isEmpty() -> {
                    EmptyStateScreen(
                        title = "No Media Found",
                        message = "This library doesn't contain any media items.",
                        onAction = { viewModel.loadMediaItems(refresh = true) },
                        actionText = "Refresh"
                    )
                }
                else -> {
                    MediaGrid(
                        mediaItems = mediaItems,
                        server = server,
                        onMediaClick = { mediaItem ->
                            navController.navigate("media_detail/$serverId/${mediaItem.ratingKey}")
                        },
                        isLoadingMore = isLoadingMore
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaGrid(
    mediaItems: List<PlexMediaItem>,
    server: com.shareconnect.plexconnect.data.model.PlexServer?,
    onMediaClick: (PlexMediaItem) -> Unit,
    isLoadingMore: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mediaItems) { mediaItem ->
            MediaCard(
                mediaItem = mediaItem,
                server = server,
                onClick = { onMediaClick(mediaItem) }
            )
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaCard(
    mediaItem: PlexMediaItem,
    server: com.shareconnect.plexconnect.data.model.PlexServer?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.67f) // Poster aspect ratio
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Media poster/thumbnail
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val posterUrl = mediaItem.thumb?.takeIf { it.isNotBlank() }

                if (posterUrl != null && server != null) {
                    val imageUrl = "${server.baseUrl}$posterUrl?X-Plex-Token=${server.token}"
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = mediaItem.title,
                        contentScale = ContentScale.Crop,
                        imageLoader = PlexImageLoader.getImageLoader(LocalContext.current),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder for media without poster
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoFile,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Watched indicator
                if (mediaItem.isWatched) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                            contentDescription = "Watched",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(2.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // Progress indicator for partially watched items
                if (mediaItem.isPartiallyWatched) {
                    LinearProgressIndicator(
                        progress = { mediaItem.progressPercentage / 100f },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                }
            }

            // Media info
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )

                mediaItem.year?.let { year ->
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateScreen(
    title: String,
    message: String,
    onAction: () -> Unit,
    actionText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onAction) {
            Text(actionText)
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}