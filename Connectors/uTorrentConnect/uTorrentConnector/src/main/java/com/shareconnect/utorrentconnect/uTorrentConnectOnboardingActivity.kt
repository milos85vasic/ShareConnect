package com.shareconnect.utorrentconnect

import android.content.Intent
import android.os.Bundle
import com.shareconnect.onboarding.ui.OnboardingActivity

class uTorrentConnectOnboardingActivity : OnboardingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set app-specific information
        appName = "uTorrentConnect"
        appDescription = "Connect to your uTorrent client and manage your downloads seamlessly. Set up your server connection, choose your theme, and select your language."

        // Initialize viewModel with sync managers from uTorrentRemote
        val app = application as uTorrentRemote
        viewModel.initializeSyncManagers(
            app.themeSyncManager,
            app.profileSyncManager,
            app.languageSyncManager
        )
    }

    override fun launchMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}