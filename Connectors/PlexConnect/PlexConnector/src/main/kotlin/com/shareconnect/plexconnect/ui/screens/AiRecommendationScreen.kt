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


package com.shareconnect.plexconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.service.PlexAiRecommendationService
import com.shareconnect.plexconnect.ui.components.MediaItemCard
import com.shareconnect.plexconnect.ui.viewmodels.PlexAiRecommendationViewModel
import com.shareconnect.plexconnect.ui.viewmodels.PlexAiRecommendationViewModelFactory
import org.koin.androidx.compose.koinViewModel

/**
 * Screen displaying AI-powered media recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecommendationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMediaDetail: (PlexMediaItem) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlexAiRecommendationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val enhancedMediaItems by viewModel.enhancedMediaItems.collectAsStateWithLifecycle()
    val similarMedia by viewModel.similarMedia.collectAsStateWithLifecycle()
    val crossLingualRecommendations by viewModel.crossLingualRecommendations.collectAsStateWithLifecycle()
    val historyBasedRecommendations by viewModel.historyBasedRecommendations.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("AI Recommendations") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refreshAll() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                        Button(onClick = { viewModel.refreshAll() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cross-lingual Recommendations Section
                    if (crossLingualRecommendations.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Cross-Lingual Recommendations",
                                subtitle = "Content in different languages"
                            )
                        }
                        items(crossLingualRecommendations) { enhancedItem ->
                            EnhancedMediaItemCard(
                                enhancedItem = enhancedItem,
                                onClick = { onNavigateToMediaDetail(enhancedItem.originalItem) }
                            )
                        }
                    }

                    // History-based Recommendations Section
                    if (historyBasedRecommendations.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Based on Your History",
                                subtitle = "Similar to content you've watched"
                            )
                        }
                        items(historyBasedRecommendations) { similarResult ->
                            SimilarMediaCard(
                                similarResult = similarResult,
                                onClick = { onNavigateToMediaDetail(similarResult.mediaItem) }
                            )
                        }
                    }

                    // All Enhanced Media Section
                    if (enhancedMediaItems.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Enhanced Media",
                                subtitle = "Media with AI-powered analysis"
                            )
                        }
                        items(enhancedMediaItems) { enhancedItem ->
                            EnhancedMediaItemCard(
                                enhancedItem = enhancedItem,
                                onClick = { onNavigateToMediaDetail(enhancedItem.originalItem) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EnhancedMediaItemCard(
    enhancedItem: PlexAiRecommendationService.EnhancedMediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Media Item Content
            Text(
                text = enhancedItem.originalItem.title ?: "Untitled",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            enhancedItem.originalItem.summary?.let { summary ->
                Text(
                    text = summary.take(200),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }

            // AI Analysis Information
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = enhancedItem.semanticLanguage.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Source Badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = enhancedItem.embeddingSource.name.lowercase().replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Error Indicator
                enhancedItem.analysisError?.let { error ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = "Error",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Metadata Analysis
            enhancedItem.metadataAnalysis?.let { analysis ->
                if (analysis.genres.isNotEmpty()) {
                    Text(
                        text = "Genres: ${analysis.genres.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (analysis.keywords.isNotEmpty()) {
                    Text(
                        text = "Keywords: ${analysis.keywords.take(5).joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SimilarMediaCard(
    similarResult: PlexAiRecommendationService.SimilarMediaResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = similarResult.mediaItem.title ?: "Untitled",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            similarResult.mediaItem.summary?.let { summary ->
                Text(
                    text = summary.take(150),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            // Similarity Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Similarity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when {
                        similarResult.similarity >= 0.8 -> MaterialTheme.colorScheme.primaryContainer
                        similarResult.similarity >= 0.6 -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                ) {
                    Text(
                        text = "${(similarResult.similarity * 100).toInt()}%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            similarResult.similarity >= 0.8 -> MaterialTheme.colorScheme.onPrimaryContainer
                            similarResult.similarity >= 0.6 -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onTertiaryContainer
                        }
                    )
                }
            }
        }
    }
}