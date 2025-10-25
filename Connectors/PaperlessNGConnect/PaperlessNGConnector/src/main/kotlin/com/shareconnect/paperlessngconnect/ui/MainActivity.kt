package com.shareconnect.paperlessngconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.paperlessngconnect.PaperlessNGConnectApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as PaperlessNGConnectApplication
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PaperlessScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperlessScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("PaperlessNGConnect") }) }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Document Management - OCR, tagging, PDF viewing, search")
        }
    }
}
