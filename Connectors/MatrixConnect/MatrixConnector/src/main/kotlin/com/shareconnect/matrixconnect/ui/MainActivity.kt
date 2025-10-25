package com.shareconnect.matrixconnect.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shareconnect.matrixconnect.ui.onboarding.MatrixConnectOnboardingActivity
import com.shareconnect.matrixconnect.ui.theme.MatrixConnectTheme
import com.shareconnect.securityaccess.SecurityAccess

/**
 * Main activity for MatrixConnect
 *
 * Implements SecurityAccess for biometric/PIN authentication
 * Checks for onboarding completion before showing main UI
 */
class MainActivity : ComponentActivity() {

    private lateinit var securityAccess: SecurityAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccess
        securityAccess = SecurityAccess(this)

        // Check if onboarding is completed
        val sharedPrefs = getSharedPreferences("matrix_prefs", MODE_PRIVATE)
        val onboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // Launch onboarding activity
            val intent = Intent(this, MatrixConnectOnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Check if security access is enabled
        val securityEnabled = sharedPrefs.getBoolean("security_enabled", false)

        if (securityEnabled) {
            // Authenticate user with biometric or PIN
            securityAccess.authenticate(
                title = "MatrixConnect Authentication",
                subtitle = "Unlock to access your encrypted messages",
                onSuccess = {
                    showMainUI()
                },
                onError = { errorCode, errorMessage ->
                    // Handle authentication error
                    finish()
                },
                onFailed = {
                    // Authentication failed
                    finish()
                }
            )
        } else {
            // No security required, show main UI
            showMainUI()
        }
    }

    private fun showMainUI() {
        setContent {
            MatrixConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MatrixConnectApp()
                }
            }
        }
    }
}
