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


package com.shareconnect.syncthingconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.securityaccess.SecurityAccessManager
import com.shareconnect.syncthingconnect.SyncthingConnectApplication

class MainActivity : ComponentActivity() {
    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as SyncthingConnectApplication
        val themeSyncManager = app.getThemeSyncManager()
        securityAccessManager = SecurityAccessManager(this)

        if (securityAccessManager.isSecurityEnabled()) {
            securityAccessManager.authenticate(
                onSuccess = { showMainUI(themeSyncManager) },
                onFailure = { finish() }
            )
        } else {
            showMainUI(themeSyncManager)
        }
    }

    private fun showMainUI(themeSyncManager: com.shareconnect.themesync.ThemeSyncManager) {
        setContent {
            val themeState by themeSyncManager.currentThemeState.collectAsState()

            ShareConnectTheme(
                darkTheme = themeState?.isDark ?: false,
                customTheme = themeState?.theme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SyncthingConnectScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncthingConnectScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Folders", "Devices", "Status", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SyncthingConnect") })
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> Text("Folders - Manage sync folders")
                1 -> Text("Devices - Connected devices")
                2 -> Text("Status - Sync status and statistics")
                3 -> Text("Settings - Configure Syncthing")
            }
        }
    }
}
