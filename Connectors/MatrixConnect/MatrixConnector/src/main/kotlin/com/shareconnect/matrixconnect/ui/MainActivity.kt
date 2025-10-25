package com.shareconnect.matrixconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shareconnect.designsystem.theme.ShareConnectTheme
import com.shareconnect.matrixconnect.MatrixConnectApplication
import com.shareconnect.securityaccess.SecurityAccessManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as MatrixConnectApplication
        val securityAccessManager = SecurityAccessManager(this)
        
        if (securityAccessManager.isSecurityEnabled()) {
            securityAccessManager.authenticate(
                onSuccess = { showUI(app) },
                onFailure = { finish() }
            )
        } else {
            showUI(app)
        }
    }

    private fun showUI(app: MatrixConnectApplication) {
        setContent {
            val theme by app.getThemeSyncManager().currentThemeState.collectAsState()
            ShareConnectTheme(darkTheme = theme?.isDark ?: false, customTheme = theme?.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MatrixScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("MatrixConnect") }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text("Matrix E2EE Messaging - Encrypted chat rooms, device keys, E2E encryption")
        }
    }
}
