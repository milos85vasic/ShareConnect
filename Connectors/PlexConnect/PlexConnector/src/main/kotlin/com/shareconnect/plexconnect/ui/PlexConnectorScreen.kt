package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import org.koin.androidx.compose.koinViewModel
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexLibrarySection
import com.shareconnect.plexconnect.data.model.PlexMediaItem
// import com.shareconnect.plexconnect.ui.viewmodels.PlexViewModel

/**
 * Main screen for Plex Connector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlexConnectorScreen(
    // viewModel: PlexViewModel = koinViewModel()
) {
    // Temporary hardcoded data
    val libraries = remember {
        listOf(
            PlexLibrarySection(
                key = "1",
                title = "Movies",
                type = com.shareconnect.plexconnect.data.model.LibraryType.MOVIE
            ),
            PlexLibrarySection(
                key = "2", 
                title = "TV Shows",
                type = com.shareconnect.plexconnect.data.model.LibraryType.SHOW
            )
        )
    }
    
    val mediaItems = remember {
        listOf(
            PlexMediaItem(
                ratingKey = "1",
                key = "/library/movies/1",
                type = MediaType.MOVIE,
                title = "Sample Movie",
                summary = "A sample movie for testing",
                year = 2023,
                serverId = 0L
            )
        )
    }
    
    val isLoading = remember { false }
    val error = remember { null as String? }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Libraries section
        item {
            Text(
                text = "Libraries",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        items(libraries) { library ->
            LibraryItem(library = library)
        }
        
        // Media items section
        item {
            Text(
                text = "Recent Media",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        items(mediaItems) { mediaItem ->
            SimpleMediaItemCard(
                mediaItem = mediaItem,
                onClick = { /* Handle click */ }
            )
        }
        
        // Loading indicator
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        // Error message
        error?.let { errorMsg ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = errorMsg,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleMediaItemCard(
    mediaItem: PlexMediaItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = mediaItem.title ?: "Unknown Title",
                style = MaterialTheme.typography.titleMedium
            )
            mediaItem.year?.let { year ->
                Text(
                    text = "Year: $year",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            mediaItem.type?.let { type ->
                Text(
                    text = "Type: ${type.value}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LibraryItem(
    library: PlexLibrarySection
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { /* Handle library click */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = library.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Type: ${library.type.value}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}