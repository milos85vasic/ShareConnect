package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel

/**
 * Main screen for Plex Connector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlexConnectorScreen(
    viewModel: PlexViewModel = getViewModel()
) {
    // Collect UI states
    val serverInfo by viewModel.serverInfo.collectAsState()
    val libraries by viewModel.libraries.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Input states
    var serverUrl by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plex Connector") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Server Connection Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("Server URL") },
                    modifier = Modifier.weight(1f)
                )
                TextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { 
                        viewModel.fetchServerInfo(serverUrl, token)
                        viewModel.fetchLibraries(serverUrl, token)
                    }
                ) {
                    Text("Connect")
                }
            }

            // Server Info Display
            serverInfo?.let { info ->
                Text(
                    "Server: ${info.myPlexUsername} (${info.machineIdentifier})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Libraries Section
            Text(
                "Libraries",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(libraries) { library ->
                    LibraryItem(
                        library = library,
                        onSelect = { 
                            viewModel.fetchLibraryItems(
                                serverUrl, 
                                library.key, 
                                token
                            )
                        }
                    )
                }
            }

            // Search Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Media") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { 
                        viewModel.searchMedia(
                            serverUrl, 
                            searchQuery, 
                            token
                        )
                    }
                ) {
                    Text("Search")
                }
            }

            // Media Items Section
            Text(
                "Media Items",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(mediaItems) { item ->
                    MediaItemCard(
                        mediaItem = item,
                        onMarkAsPlayed = { 
                            viewModel.markAsPlayed(
                                serverUrl, 
                                item.ratingKey ?: return@MediaItemCard, 
                                token
                            )
                        }
                    )
                }
            }

            // Loading and Error Handling
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}

/**
 * Composable for displaying a library item
 */
@Composable
fun LibraryItem(
    library: PlexLibrarySection,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = library.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Type: ${library.type}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Composable for displaying a media item
 */
@Composable
fun MediaItemCard(
    mediaItem: PlexMediaItem,
    onMarkAsPlayed: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mediaItem.title ?: "Untitled",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Type: ${mediaItem.type}",
                    style = MaterialTheme.typography.bodyMedium
                )
                mediaItem.year?.let { year ->
                    Text(
                        text = "Year: $year",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Button(onClick = onMarkAsPlayed) {
                Text("Mark Played")
            }
        }
    }
}