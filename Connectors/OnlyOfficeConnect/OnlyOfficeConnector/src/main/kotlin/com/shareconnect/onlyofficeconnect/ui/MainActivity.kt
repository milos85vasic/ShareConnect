package com.shareconnect.onlyofficeconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.onlyofficeconnect.OnlyOfficeConnectApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as OnlyOfficeConnectApplication
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false) {
                OnlyOfficeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlyOfficeScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("OnlyOfficeConnect") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Document Editing - WebView integration, collaborative editing, document conversion")
        }
    }
}
