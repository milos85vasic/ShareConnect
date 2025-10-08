package com.shareconnect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.shareconnect.languagesync.utils.LocaleHelper

/**
 * Base activity that applies locale changes
 * All activities should extend this to support language sync
 */
open class BaseLocaleActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }
}
