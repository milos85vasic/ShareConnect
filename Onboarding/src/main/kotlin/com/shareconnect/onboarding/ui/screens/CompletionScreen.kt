package com.shareconnect.onboarding.ui.screens

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import com.airbnb.lottie.compose.*

@Composable
fun CompletionScreen(
    onComplete: () -> Unit
) {
    val scrollState = rememberScrollState()
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated completion illustration
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.shareconnect.onboarding.R.raw.completion_animation))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp)
        )

        // Completion title
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_completion_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Completion message
        Text(
            text = stringResource(com.shareconnect.onboarding.R.string.onboarding_completion_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Start using app button
        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = stringResource(com.shareconnect.onboarding.R.string.onboarding_completion_start),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}