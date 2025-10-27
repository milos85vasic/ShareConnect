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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import com.shareconnect.plexconnect.ui.viewmodels.LibraryListViewModel
import com.shareconnect.plexconnect.ui.viewmodels.LibraryListViewModelFactory
import com.shareconnect.plexconnect.data.model.PlexLibrary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(navController: NavController, serverId: Long) {
    val context = LocalContext.current
    val serverRepository = remember { DependencyContainer.plexServerRepository }
    val libraryRepository = remember { DependencyContainer.plexLibraryRepository }

    val viewModel = viewModel<LibraryListViewModel>(
        factory = LibraryListViewModelFactory(serverRepository, libraryRepository, serverId)
    )

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val server by viewModel.server.collectAsState()
    val libraries by viewModel.libraries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(server?.name ?: "Libraries")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshLibraries() }) {
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
                isLoading && libraries.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    ErrorScreen(
                        error = error!!,
                        onRetry = { viewModel.refreshLibraries() }
                    )
                }
                libraries.isEmpty() -> {
                    EmptyStateScreen(
                        title = "No Libraries Found",
                        message = "This server doesn't have any libraries or they couldn't be loaded.",
                        onAction = { viewModel.refreshLibraries() },
                        actionText = "Refresh"
                    )
                }
                else -> {
                    LibraryGrid(
                        libraries = libraries,
                        server = server,
                        onLibraryClick = { library ->
                            navController.navigate("media/$serverId/${library.key}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryGrid(
    libraries: List<PlexLibrary>,
    server: com.shareconnect.plexconnect.data.model.PlexServer?,
    onLibraryClick: (PlexLibrary) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(libraries) { library ->
            LibraryCard(
                library = library,
                server = server,
                onClick = { onLibraryClick(library) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryCard(
    library: PlexLibrary,
    server: com.shareconnect.plexconnect.data.model.PlexServer?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Library thumbnail/artwork
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val artworkUrl = library.composite.takeIf { it.isNotBlank() }
                    ?: library.art.takeIf { it.isNotBlank() }
                    ?: library.thumb.takeIf { it.isNotBlank() }

                if (artworkUrl != null && server != null) {
                    val imageUrl = "${server.baseUrl}$artworkUrl?X-Plex-Token=${server.token}"
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = library.title,
                        contentScale = ContentScale.Crop,
                        imageLoader = PlexImageLoader.getImageLoader(LocalContext.current),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder for libraries without artwork
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Library info
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = library.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                Text(
                    text = library.type.value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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