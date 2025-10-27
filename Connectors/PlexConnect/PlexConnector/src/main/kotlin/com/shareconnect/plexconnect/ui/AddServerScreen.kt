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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shareconnect.plexconnect.di.DependencyContainer
import com.shareconnect.plexconnect.ui.viewmodels.AddServerViewModel
import com.shareconnect.plexconnect.ui.viewmodels.AddServerViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServerScreen(navController: NavController) {
    val context = LocalContext.current
    val serverRepository = remember { DependencyContainer.plexServerRepository }
    val authService = remember { DependencyContainer.plexAuthService }

    val viewModel = viewModel<AddServerViewModel>(
        factory = AddServerViewModelFactory(serverRepository, authService)
    )

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    val requiresAuth by viewModel.requiresAuth.collectAsState()

    // Navigate back on success
    LaunchedEffect(success) {
        if (success) {
            navController.popBackStack()
        }
    }

    // Navigate to authentication when required
    LaunchedEffect(requiresAuth) {
        if (requiresAuth) {
            navController.navigate("authenticate")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Plex Server") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AddServerForm(
                    onTestServer = { name, address, port ->
                        viewModel.testServerConnection(name, address, port)
                    },
                    error = error,
                    onErrorDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddServerForm(
    onTestServer: (String, String, Int) -> Unit,
    error: String?,
    onErrorDismiss: () -> Unit
) {
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("32400") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enter your Plex Media Server details",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = serverName,
            onValueChange = { serverName = it },
            label = { Text("Server Name") },
            placeholder = { Text("My Plex Server") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = serverAddress,
            onValueChange = { serverAddress = it },
            label = { Text("Server Address") },
            placeholder = { Text("192.168.1.100 or plex.example.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text("Port") },
            placeholder = { Text("32400") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val port = serverPort.toIntOrNull() ?: 32400
                onTestServer(serverName, serverAddress, port)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_server_button"),
            enabled = serverName.isNotBlank() && serverAddress.isNotBlank()
        ) {
            Text("Test Connection")
        }
    }
}