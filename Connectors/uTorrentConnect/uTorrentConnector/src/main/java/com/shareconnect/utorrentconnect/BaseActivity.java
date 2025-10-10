package com.shareconnect.utorrentconnect;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.shareconnect.languagesync.utils.LocaleHelper;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void onBackPressed() {
        boolean handled = handleBackPressByFragments();
        if (!handled) super.onBackPressed();
    }

    /**
     * @return {@code true} if back press handled by visible fragments
     */
    protected boolean handleBackPressByFragments() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof OnBackPressedListener && fragment.isVisible()) {
                boolean handled = ((OnBackPressedListener) fragment).onBackPressed();
                if (handled) return true;
            }
        }
        return false;
    }
}
