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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shareconnect.plexconnect.config.NlpConfig
import com.shareconnect.plexconnect.ui.viewmodels.AiRecommendationSettingsViewModel
import com.shareconnect.plexconnect.ui.viewmodels.AiRecommendationSettingsViewModelFactory
import org.koin.androidx.compose.koinViewModel

/**
 * Settings screen for AI recommendations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRecommendationSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AiRecommendationSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("AI Recommendation Settings") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.resetToDefaults() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset to defaults")
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
                        Button(onClick = { viewModel.loadSettings() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Enable AI Recommendations
                    item {
                        SettingsToggleCard(
                            title = "Enable AI Recommendations",
                            subtitle = "Use semantic analysis for content suggestions",
                            icon = Icons.Default.Info,
                            checked = uiState.enableAiRecommendations,
                            onCheckedChange = viewModel::setEnableAiRecommendations
                        )
                    }
                    
                    // Similarity Threshold
                    item {
                        SettingsSliderCard(
                            title = "Similarity Threshold",
                            subtitle = "How similar content should be for recommendations (${String.format("%.0f", uiState.similarityThreshold * 100)}%)",
                            value = uiState.similarityThreshold.toFloat(),
                            onValueChange = { viewModel.setSimilarityThreshold(it.toDouble()) },
                            valueRange = 0.3f..0.9f,
                            steps = 13
                        )
                    }
                    
                    // Recommendation Count
                    item {
                        SettingsSliderCard(
                            title = "Number of Recommendations",
                            subtitle = "Maximum recommendations to show (${uiState.recommendationCount})",
                            value = uiState.recommendationCount.toFloat(),
                            onValueChange = { viewModel.setRecommendationCount(it.toInt()) },
                            valueRange = 5f..50f,
                            steps = 9
                        )
                    }
                    
                    // Language Preference
                    item {
                        SettingsSelectionCard(
                            title = "Content Language",
                            subtitle = "Preferred language for recommendations",
                            options = NlpConfig.SUPPORTED_LANGUAGES.toList(),
                            selectedOption = uiState.languagePreference,
                            onOptionSelected = viewModel::setLanguagePreference,
                            optionLabels = mapOf(
                                "en" to "English",
                                "zh" to "中文",
                                "ja" to "日本語",
                                "ko" to "한국어",
                                "ar" to "العربية",
                                "hi" to "हिन्दी",
                                "es" to "Español",
                                "fr" to "Français",
                                "de" to "Deutsch",
                                "ru" to "Русский"
                            )
                        )
                    }
                    
                    // Cross-Lingual Recommendations
                    item {
                        SettingsToggleCard(
                            title = "Cross-Lingual Recommendations",
                            subtitle = "Include content in other languages",
                            icon = Icons.Default.Info,
                            checked = uiState.enableCrossLingual,
                            onCheckedChange = viewModel::setEnableCrossLingual
                        )
                    }
                    
                    // Recommendation Types
                    item {
                        SettingsMultiSelectCard(
                            title = "Recommendation Types",
                            subtitle = "Choose which recommendation types to include",
                            options = RecommendationType.values().toList(),
                            selectedOptions = uiState.enabledRecommendationTypes,
                            onOptionsSelected = viewModel::setEnabledRecommendationTypes,
                            optionLabels = mapOf(
                                RecommendationType.SEMANTIC to "Similar Content",
                                RecommendationType.COLLABORATIVE to "Popular with Similar Users",
                                RecommendationType.TRENDING to "Trending Now",
                                RecommendationType.PERSONALIZED to "Personalized for You"
                            )
                        )
                    }
                    
                    // Advanced Settings
                    item {
                        SettingsSectionHeader(
                            title = "Advanced Settings",
                            subtitle = "Fine-tune recommendation behavior"
                        )
                    }
                    
                    // Cache Settings
                    item {
                        SettingsSliderCard(
                            title = "Cache Size",
                            subtitle = "Number of items to keep in memory (${uiState.cacheSize})",
                            value = uiState.cacheSize.toFloat(),
                            onValueChange = { viewModel.setCacheSize(it.toInt()) },
                            valueRange = 100f..2000f,
                            steps = 19
                        )
                    }
                    
                    // Performance Monitoring
                    item {
                        SettingsToggleCard(
                            title = "Performance Monitoring",
                            subtitle = "Track recommendation performance metrics",
                            icon = Icons.Default.Info,
                            checked = uiState.enablePerformanceMonitoring,
                            onCheckedChange = viewModel::setEnablePerformanceMonitoring
                        )
                    }
                    
                    // Debug Mode
                    item {
                        SettingsToggleCard(
                            title = "Debug Mode",
                            subtitle = "Show detailed recommendation information",
                            icon = Icons.Default.Info,
                            checked = uiState.debugMode,
                            onCheckedChange = viewModel::setDebugMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingsSliderCard(
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps
            )
        }
    }
}

@Composable
private fun <T> SettingsSelectionCard(
    title: String,
    subtitle: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabels: Map<T, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option == selectedOption,
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionLabels[option] ?: option.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> SettingsMultiSelectCard(
    title: String,
    subtitle: String,
    options: List<T>,
    selectedOptions: Set<T>,
    onOptionsSelected: (Set<T>) -> Unit,
    optionLabels: Map<T, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .selectable(
                            selected = option in selectedOptions,
                            onClick = {
                                val newSelection = if (option in selectedOptions) {
                                    selectedOptions - option
                                } else {
                                    selectedOptions + option
                                }
                                onOptionsSelected(newSelection)
                            },
                            role = Role.Checkbox
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = option in selectedOptions,
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = optionLabels[option] ?: option.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
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

enum class RecommendationType {
    SEMANTIC,
    COLLABORATIVE,
    TRENDING,
    PERSONALIZED
}