package com.shareconnect.utorrentconnect;

import android.content.Intent;
import com.shareconnect.onboarding.ui.OnboardingActivity;
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel;

public class uTorrentConnectOnboardingActivity extends OnboardingActivity {

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize viewModel with sync managers from uTorrentRemote
        uTorrentRemote app = (uTorrentRemote) getApplication();
        viewModel.initializeSyncManagers(
            app.themeSyncManager,
            app.profileSyncManager,
            app.languageSyncManager
        );
    }

    @Override
    protected void launchMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}