package com.shareconnect.onboarding.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()

    // Mock theme options - in real app, these would come from ThemeSyncManager
    val themes = listOf(
        ThemeData("light", "Light Theme", "material", false, true, "com.shareconnect"),
        ThemeData("dark", "Dark Theme", "material", true, false, "com.shareconnect"),
        ThemeData("system", "System Theme", "material", false, false, "com.shareconnect")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_theme_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Message
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_theme_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Theme options
        themes.forEach { theme ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { viewModel.selectTheme(theme) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTheme?.id == theme.id)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = theme.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (theme.isDarkMode) "Dark theme" else "Light theme",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(com.shareconnect.onboarding.R.string.onboarding_back))
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = selectedTheme != null
            ) {
                Text(stringResource(com.shareconnect.onboarding.R.string.onboarding_next))
            }
        }
    }
}