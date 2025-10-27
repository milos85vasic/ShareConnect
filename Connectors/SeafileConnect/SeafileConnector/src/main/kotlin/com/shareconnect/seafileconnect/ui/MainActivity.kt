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


package com.shareconnect.seafileconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.seafileconnect.SeafileConnectApplication
import com.shareconnect.securityaccess.SecurityAccessManager
import com.shareconnect.themesync.ThemeSyncManager
import kotlinx.coroutines.launch

/**
 * MainActivity for SeafileConnect
 * 
 * Provides access to Seafile server functionality with:
 * - Library browsing
 * - File management
 * - Upload/download capabilities
 * - Search functionality
 * - Encrypted library support
 */
class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as SeafileConnectApplication
        themeSyncManager = application.getThemeSyncManager()
        securityAccessManager = SecurityAccessManager(this)

        // Check security before showing main UI
        if (securityAccessManager.isSecurityEnabled()) {
            securityAccessManager.authenticate(
                onSuccess = { showMainUI() },
                onFailure = { finish() }
            )
        } else {
            showMainUI()
        }
    }

    private fun showMainUI() {
        setContent {
            val themeState by themeSyncManager.currentThemeState.collectAsState()

            ShareConnectTheme(
                darkTheme = themeState?.isDark ?: false,
                customTheme = themeState?.theme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SeafileConnectScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeafileConnectScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Libraries", "Files", "Search", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SeafileConnect") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Text(title.first().toString()) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> LibrariesTab()
                1 -> FilesTab()
                2 -> SearchTab()
                3 -> SettingsTab()
            }
        }
    }
}

@Composable
fun LibrariesTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Libraries - Browse and manage Seafile libraries",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}

@Composable
fun FilesTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Files - Browse files and directories",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}

@Composable
fun SearchTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Search - Search across all libraries",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}

@Composable
fun SettingsTab() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "Settings - Configure SeafileConnect",
            modifier = Modifier.padding(MaterialTheme.typography.bodyLarge.fontSize.value.toInt().dp)
        )
    }
}
