package com.shareconnect.duplicaticonnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.duplicaticonnect.DuplicatiConnectApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as DuplicatiConnectApplication
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DuplicatiScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicatiScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("DuplicatiConnect") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Backup Management - Backup jobs, restore operations, progress monitoring")
        }
    }
}
