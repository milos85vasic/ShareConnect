package com.shareconnect.duplicaticonnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import digital.vasic.security.access.ui.SecurityAccess
import digital.vasic.security.access.ui.SecurityAccessViewModel
import digital.vasic.security.access.database.PinLength
import com.shareconnect.duplicaticonnect.DuplicatiConnectApplication

/**
 * Main activity for DuplicatiConnect
 * Backup management and file restoration UI with SecurityAccess integration
 */
class MainActivity : ComponentActivity() {

    private lateinit var securityViewModel: SecurityAccessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccess ViewModel
        securityViewModel = SecurityAccessViewModel(application)

        setContent {
            DuplicatiConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SecurityAccess(
                        viewModel = securityViewModel,
                        pinLength = PinLength.FOUR,
                        onAuthenticationSuccess = {
                            // Authentication successful - show main content
                        }
                    ) {
                        DuplicatiConnectContent()
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicatiConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Composable
fun DuplicatiConnectContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DuplicatiConnect",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Manage your Duplicati backups and restore files",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                FeatureItem("• Backup job management")
                FeatureItem("• Run backups on demand")
                FeatureItem("• Restore files and folders")
                FeatureItem("• Monitor backup progress")
                FeatureItem("• View backup logs and history")
                FeatureItem("• Multiple storage backends")
                FeatureItem("• Incremental backup support")
                FeatureItem("• Schedule backup operations")
            }
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
