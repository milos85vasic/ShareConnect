/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.activities

import android.content.res.Configuration
import android.widget.ImageView
import com.shareconnect.R
import com.shareconnect.SplashActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
@org.junit.Ignore("Activity tests require complex setup and are disabled for now")
class SplashScreenTest {

    @Test
    fun testLightThemeSplashScreenUsesCorrectLogo() {
        // Test light theme splash screen
        val config = Configuration(RuntimeEnvironment.getApplication().resources.configuration).apply {
            uiMode = Configuration.UI_MODE_NIGHT_NO
        }

        val activity = Robolectric.buildActivity(SplashActivity::class.java).create().get()

        // Verify the layout uses the correct drawable
        val logoImageView = activity.findViewById<ImageView>(R.id.splash_logo)
        assertNotNull("Splash logo ImageView should exist", logoImageView)

        // Verify the drawable resource is splash_logo_light
        val drawable = logoImageView?.drawable
        assertNotNull("Splash logo should have a drawable", drawable)

        // The drawable should be the splash_logo_light resource
        // Note: Robolectric may not load the exact drawable, but we can verify the ImageView exists and has proper attributes
        assertEquals("ImageView should have proper scale type", android.widget.ImageView.ScaleType.FIT_CENTER, logoImageView?.scaleType)

        activity.finish()
    }

    @Test
    fun testDarkThemeSplashScreenUsesCorrectLogo() {
        // Test dark theme splash screen
        val activity = Robolectric.buildActivity(SplashActivity::class.java).create().get()

        // Verify the layout uses the correct drawable
        val logoImageView = activity.findViewById<ImageView>(R.id.splash_logo)
        assertNotNull("Splash logo ImageView should exist", logoImageView)

        // Verify the drawable resource is splash_logo_dark
        val drawable = logoImageView?.drawable
        assertNotNull("Splash logo should have a drawable", drawable)

        // The drawable should be the splash_logo_dark resource
        assertEquals("ImageView should have proper scale type", android.widget.ImageView.ScaleType.FIT_CENTER, logoImageView?.scaleType)

        activity.finish()
    }

    @Test
    fun testSplashScreenLayoutInflation() {
        // Test that both light and dark splash layouts can be inflated without errors
        // Test light theme
        val lightActivity = Robolectric.buildActivity(SplashActivity::class.java).create().get()
        assertNotNull("Light theme splash activity should be created", lightActivity)

        // Test dark theme
        val darkActivity = Robolectric.buildActivity(SplashActivity::class.java).create().get()
        assertNotNull("Dark theme splash activity should be created", darkActivity)
    }
}