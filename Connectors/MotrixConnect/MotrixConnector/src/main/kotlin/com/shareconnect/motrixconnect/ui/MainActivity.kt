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


package com.shareconnect.motrixconnect.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.motrixconnect.data.api.MotrixApiClient
import com.shareconnect.motrixconnect.data.model.MotrixDownload
import com.shareconnect.qrscanner.QRScannerManager
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccessManager first
        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if security access is required BEFORE setting up UI
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }

        // Show main UI
        setupMainView()
    }

    override fun onResume() {
        super.onResume()

        // Check security access on resume (when coming back from background)
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavController) {
    val context = LocalContext.current
    var downloads by remember { mutableStateOf<List<MotrixDownload>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: Get server URL from settings/preferences
    val apiClient = remember { MotrixApiClient("http://localhost:16800") }

    LaunchedEffect(Unit) {
        loadDownloads(apiClient, { downloads = it }, { isLoading = it }, { errorMessage = it })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_download") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Download")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Downloads",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No downloads yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to add a download",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(downloads) { download ->
                        DownloadItemCard(download)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDownloadScreen(navController: NavController) {
    val context = LocalContext.current
    var urlText by remember { mutableStateOf("") }
    var showAddOptionsDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: Get server URL from settings/preferences
    val apiClient = remember { MotrixApiClient("http://localhost:16800") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Download") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Download",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text("Download URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { showAddOptionsDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan QR")
                }

                Button(
                    onClick = {
                        if (urlText.isNotBlank()) {
                            coroutineScope.launch {
                                addDownload(apiClient, urlText) { success ->
                                    if (success) {
                                        navController.popBackStack()
                                    }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(navController: NavController) {
    val context = LocalContext.current
    var downloads by remember { mutableStateOf<List<MotrixDownload>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: Get server URL from settings/preferences
    val apiClient = remember { MotrixApiClient("http://localhost:16800") }

    LaunchedEffect(Unit) {
        loadDownloads(apiClient, { downloads = it }, { isLoading = it }, { errorMessage = it })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_download") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Download")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Downloads",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No downloads yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to add a download",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(downloads) { download ->
                        DownloadItemCard(download)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDownloadScreen(navController: NavController) {
    val context = LocalContext.current
    var urlText by remember { mutableStateOf("") }
    var showAddOptionsDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: Get server URL from settings/preferences
    val apiClient = remember { MotrixApiClient("http://localhost:16800") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Download") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Download",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text("Download URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { showAddOptionsDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan QR")
                }

                Button(
                    onClick = {
                        if (urlText.isNotBlank()) {
                            coroutineScope.launch {
                                addDownload(apiClient, urlText) { success ->
                                    if (success) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = urlText.isNotBlank()
                ) {
                    Text("Add Download")
                }
            }
        }

        // Add Options Dialog
        if (showAddOptionsDialog) {
            AlertDialog(
                onDismissRequest = { showAddOptionsDialog = false },
                title = { Text("Add Content") },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                // Manual URL entry - already shown
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enter URL Manually")
                        }
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                coroutineScope.launch {
                                    val qrResult = QRScannerManager(context).scanQRCode()
                                    if (qrResult != null) {
                                        urlText = qrResult
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scan QR Code")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddOptionsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DownloadItemCard(download: MotrixDownload) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = download.files.firstOrNull()?.path ?: "Unknown file",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${download.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${download.completedLength}/${download.totalLength}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (download.totalLength > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = download.completedLength.toFloat() / download.totalLength.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private suspend fun loadDownloads(
    apiClient: MotrixApiClient,
    onDownloadsLoaded: (List<MotrixDownload>) -> Unit,
    onLoadingChanged: (Boolean) -> Unit,
    onError: (String?) -> Unit
) {
    onLoadingChanged(true)
    onError(null)

    try {
        val activeResult = apiClient.tellActive()
        val waitingResult = apiClient.tellWaiting()
        val stoppedResult = apiClient.tellStopped()

        val allDownloads = mutableListOf<MotrixDownload>()

        activeResult.getOrNull()?.let { allDownloads.addAll(it) }
        waitingResult.getOrNull()?.let { allDownloads.addAll(it) }
        stoppedResult.getOrNull()?.let { allDownloads.addAll(it.take(50)) } // Limit stopped downloads

        onDownloadsLoaded(allDownloads)
    } catch (e: Exception) {
        onError(e.message ?: "Failed to load downloads")
    } finally {
        onLoadingChanged(false)
    }
}

private suspend fun addDownload(
    apiClient: MotrixApiClient,
    url: String,
    onResult: (Boolean) -> Unit
) {
    try {
        val result = apiClient.addUri(url)
        onResult(result.isSuccess)
    } catch (e: Exception) {
        onResult(false)
    }
}
                    },
                    modifier = Modifier.weight(1f),
                    enabled = urlText.isNotBlank()
                ) {
                    Text("Add Download")
                }
            }
        }

        // Add Options Dialog
        if (showAddOptionsDialog) {
            AlertDialog(
                onDismissRequest = { showAddOptionsDialog = false },
                title = { Text("Add Content") },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                // Manual URL entry - already shown
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enter URL Manually")
                        }
                        TextButton(
                            onClick = {
                                showAddOptionsDialog = false
                                coroutineScope.launch {
                                    val qrResult = QRScannerManager(context).scanQRCode()
                                    if (qrResult != null) {
                                        urlText = qrResult
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scan QR Code")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddOptionsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DownloadItemCard(download: MotrixDownload) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = download.files.firstOrNull()?.path ?: "Unknown file",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${download.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${download.completedLength}/${download.totalLength}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (download.totalLength > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = download.completedLength.toFloat() / download.totalLength.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private suspend fun loadDownloads(
    apiClient: MotrixApiClient,
    onDownloadsLoaded: (List<MotrixDownload>) -> Unit,
    onLoadingChanged: (Boolean) -> Unit,
    onError: (String?) -> Unit
) {
    onLoadingChanged(true)
    onError(null)

    try {
        val activeResult = apiClient.tellActive()
        val waitingResult = apiClient.tellWaiting()
        val stoppedResult = apiClient.tellStopped()

        val allDownloads = mutableListOf<MotrixDownload>()

        activeResult.getOrNull()?.let { allDownloads.addAll(it) }
        waitingResult.getOrNull()?.let { allDownloads.addAll(it) }
        stoppedResult.getOrNull()?.let { allDownloads.addAll(it.take(50)) } // Limit stopped downloads

        onDownloadsLoaded(allDownloads)
    } catch (e: Exception) {
        onError(e.message ?: "Failed to load downloads")
    } finally {
        onLoadingChanged(false)
    }
}

private suspend fun addDownload(
    apiClient: MotrixApiClient,
    url: String,
    onResult: (Boolean) -> Unit
) {
    try {
        val result = apiClient.addUri(url)
        onResult(result.isSuccess)
    } catch (e: Exception) {
        onResult(false)
    }
}
        }
    }

    private fun isSecurityAccessRequired(): Boolean {
        return try {
            runBlocking {
                securityAccessManager.isAccessRequired()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking security access requirement", e)
            false // Default to no security if there's an error
        }
    }

    private fun launchSecurityAccess() {
        try {
            showSecurityAccessDialog()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error launching security access", e)
            // If security access fails, continue with normal flow
            setupMainView()
        }
    }

    private fun showSecurityAccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Security Access Required")
        builder.setMessage("Please enter your PIN to access MotrixConnect")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Unlock") { _: android.content.DialogInterface, _: Int ->
            val pin = input.text.toString()
            authenticateWithPin(pin)
        }

        builder.setNegativeButton("Cancel") { _: android.content.DialogInterface, _: Int ->
            finish() // Close app if user cancels
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun authenticateWithPin(pin: String) {
        lifecycleScope.launch {
            try {
                val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)
                when (result) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        // Authentication successful, show main view
                        setupMainView()
                    }
                    else -> {
                        // Authentication failed, show error and retry
                        Toast.makeText(this@MainActivity, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                        showSecurityAccessDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during PIN authentication", e)
                Toast.makeText(this@MainActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show()
                showSecurityAccessDialog()
            }
        }
    }
}
