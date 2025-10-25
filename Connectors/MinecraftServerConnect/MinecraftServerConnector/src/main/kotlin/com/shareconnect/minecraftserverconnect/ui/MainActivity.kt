package com.shareconnect.minecraftserverconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.minecraftserverconnect.MinecraftServerConnectApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as MinecraftServerConnectApplication
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false) {
                MinecraftScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinecraftScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("MinecraftServerConnect") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Minecraft Server - RCON protocol, command execution, server stats, player management")
        }
    }
}
