package com.shareconnect.onboarding.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel
import com.shareconnect.languagesync.models.LanguageData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val languages by viewModel.availableLanguages.collectAsState()

    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                top = with(density) { statusBarHeight.toDp() } + 24.dp,
                bottom = with(density) { navigationBarHeight.toDp() } + 24.dp,
                start = 24.dp,
                end = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_language_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Message
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_language_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Language options
        languages.forEach { language ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { viewModel.selectLanguage(language) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedLanguage?.languageCode == language.languageCode)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = language.languageCode.uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (selectedLanguage?.languageCode == language.languageCode) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                enabled = selectedLanguage != null
            ) {
                Text(stringResource(com.shareconnect.onboarding.R.string.onboarding_next))
            }
        }
    }
}