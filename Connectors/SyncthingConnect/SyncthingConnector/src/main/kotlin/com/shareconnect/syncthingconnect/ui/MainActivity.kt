package com.shareconnect.syncthingconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vasic.toolkit.securityaccess.SecurityAccessManager

/**
 * Main activity for SyncthingConnect
 * P2P file synchronization UI
 */
class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccess
        securityAccessManager = SecurityAccessManager(this)

        setContent {
            SyncthingConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SyncthingConnectContent()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        securityAccessManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        securityAccessManager.onPause()
    }
}

@Composable
fun SyncthingConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Composable
fun SyncthingConnectContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SyncthingConnect",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SyncthingConnectPreview() {
    SyncthingConnectTheme {
        SyncthingConnectContent()
    }
}
