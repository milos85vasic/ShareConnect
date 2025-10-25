package com.shareconnect.wireguardconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.wireguardconnect.WireGuardConnectApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as WireGuardConnectApplication
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false) {
                WireGuardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireGuardScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("WireGuardConnect") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("VPN Configuration - Config parsing, QR code generation/scanning, tunnel management")
        }
    }
}
