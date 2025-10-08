package com.shareconnect

import android.content.Intent
import com.shareconnect.onboarding.ui.OnboardingActivity
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel

class ShareConnectOnboardingActivity : OnboardingActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize viewModel with sync managers from SCApplication
        val app = application as SCApplication
        viewModel.initializeSyncManagers(
            themeManager = app.themeSyncManager,
            profileManager = app.profileSyncManager,
            languageManager = app.languageSyncManager
        )
    }

    override fun launchMainApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}