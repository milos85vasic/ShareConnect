package com.shareconnect.jdownloaderconnect.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shareconnect.jdownloaderconnect.AppContainer
import com.shareconnect.jdownloaderconnect.ui.MainActivity
import com.shareconnect.jdownloaderconnect.ui.screens.SettingsScreen
import com.shareconnect.jdownloaderconnect.ui.theme.JDownloaderConnectTheme

class SettingsActivity : ComponentActivity() {
    
    private lateinit var appContainer: AppContainer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appContainer = AppContainer(this)
        
        setContent {
            JDownloaderConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(
                        onNavigateBack = { finish() },
                        accountViewModel = appContainer.accountViewModel
                    )
                }
            }
        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to main activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}