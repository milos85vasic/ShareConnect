package com.shareconnect.plexconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.service.PlexAuthService
import com.shareconnect.plexconnect.ui.viewmodels.AuthenticationViewModel
import com.shareconnect.plexconnect.ui.viewmodels.AuthenticationViewModelFactory
import com.shareconnect.plexconnect.data.model.PlexPinResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavController) {
    val context = LocalContext.current
    val authService = remember { DependencyContainer.plexAuthService }

    val authViewModel: AuthenticationViewModel = viewModel(
        factory = AuthenticationViewModelFactory(authService)
    )

    val authState by authViewModel.authState.collectAsState()
    val isAuthenticating by authViewModel.isAuthenticating.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is PlexAuthService.AuthState.Authenticated -> {
                // Authentication successful, navigate back
                // The AddServerViewModel will handle completing the server addition
                navController.popBackStack()
            }
            is PlexAuthService.AuthState.Error -> {
                // Show error (handled in UI)
            }
            else -> {
                // Continue with current state
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plex Authentication") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (authState is PlexAuthService.AuthState.Idle ||
                        authState is PlexAuthService.AuthState.Error) {
                        IconButton(onClick = { authViewModel.startAuthentication() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Start Authentication")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = authState) {
                is PlexAuthService.AuthState.Idle -> {
                    IdleState(authViewModel)
                }
                is PlexAuthService.AuthState.RequestingPin -> {
                    RequestingPinState(state.clientId)
                }
                is PlexAuthService.AuthState.PinReceived -> {
                    PinReceivedState(state.pin)
                }
                is PlexAuthService.AuthState.CheckingPin -> {
                    CheckingPinState()
                }
                is PlexAuthService.AuthState.Authenticated -> {
                    AuthenticatedState()
                }
                is PlexAuthService.AuthState.Error -> {
                    ErrorState(state.message, authViewModel)
                }
            }
        }
    }
}

@Composable
private fun IdleState(viewModel: AuthenticationViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Plex Authentication",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Click the refresh button above to start authentication with Plex.tv",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(onClick = { viewModel.startAuthentication() }) {
            Text("Start Authentication")
        }
    }
}

@Composable
private fun RequestingPinState(clientId: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "Requesting PIN from Plex.tv...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Client ID: $clientId",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PinReceivedState(pin: PlexPinResponse) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "PIN Code",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = pin.code.toString(),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Text(
            text = "Go to https://plex.tv/link on your computer or mobile device and enter this PIN code.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Waiting for authentication...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun CheckingPinState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = "Checking authentication status...",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthenticatedState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "✓ Authentication Successful!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "You are now authenticated with Plex.tv",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    authMessage: String,
    authViewModel: AuthenticationViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Authentication Failed",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = authMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            OutlinedButton(onClick = { authViewModel.resetAuthentication() }) {
                Text("Try Again")
            }
            Button(onClick = { authViewModel.startAuthentication() }) {
                Text("Retry")
            }
        }
    }
}