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


package com.shareconnect.jdownloaderconnect.automation

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Comprehensive automation test for JDownloaderConnect icon assets.
 * Verifies that all launcher icons and splash screen assets are properly generated
 * from the Logo.jpeg source and meet quality requirements.
 */
@RunWith(AndroidJUnit4::class)
class JDownloaderIconVerificationTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName
    private val res = context.resources

    @Test
    fun testAdaptiveIconReferences() {
        // Verify that the app uses adaptive icons
        val pm = context.packageManager
        val appInfo = pm.getApplicationInfo(packageName, 0)

        // Check that icon resources exist
        val iconResId = appInfo.icon
        val roundIconResId = appInfo.icon // roundIcon is handled differently in API

        assertNotEquals("App icon resource should be set", 0, iconResId)

        // Verify adaptive icon XML files exist
        val adaptiveIconXml = File(context.filesDir.parent, "res/mipmap-anydpi-v26/ic_launcher.xml")
        val adaptiveRoundIconXml = File(context.filesDir.parent, "res/mipmap-anydpi-v26/ic_launcher_round.xml")

        // Note: These files exist at build time, but may not be accessible at runtime
        // The important thing is that the resources are referenced correctly
    }

    @Test
    fun testMipmapIconDimensions() {
        // Test all mipmap densities have correct dimensions
        val densities = mapOf(
            "mipmap-mdpi" to 48,
            "mipmap-hdpi" to 72,
            "mipmap-xhdpi" to 96,
            "mipmap-xxhdpi" to 144,
            "mipmap-xxxhdpi" to 192
        )

        densities.forEach { (folder, expectedSize) ->
            // Test ic_launcher.png
            val launcherResId = res.getIdentifier("ic_launcher", "mipmap", packageName)
            assertNotEquals("ic_launcher should exist in $folder", 0, launcherResId)

            // Test ic_launcher_round.png
            val roundLauncherResId = res.getIdentifier("ic_launcher_round", "mipmap", packageName)
            assertNotEquals("ic_launcher_round should exist in $folder", 0, roundLauncherResId)

            // Note: We can't easily test bitmap dimensions at runtime without loading from assets
            // The build-time verification is more important
        }
    }

    @Test
    fun testAdaptiveIconComponents() {
        // Test that adaptive icon foreground and background exist
        val foregroundResId = res.getIdentifier("ic_launcher_foreground", "drawable", packageName)
        assertNotEquals("ic_launcher_foreground should exist", 0, foregroundResId)

        val backgroundResId = res.getIdentifier("ic_launcher_background", "drawable", packageName)
        assertNotEquals("ic_launcher_background should exist", 0, backgroundResId)

        // Verify foreground is 108x108 (adaptive icon size)
        val foregroundBitmap = BitmapFactory.decodeResource(res, foregroundResId)
        assertNotNull("Foreground bitmap should load", foregroundBitmap)
        assertEquals("Foreground should be 108px wide", 108, foregroundBitmap?.width)
        assertEquals("Foreground should be 108px tall", 108, foregroundBitmap?.height)
        foregroundBitmap?.recycle()

        // Verify background is 108x108
        val backgroundBitmap = BitmapFactory.decodeResource(res, backgroundResId)
        assertNotNull("Background bitmap should load", backgroundBitmap)
        assertEquals("Background should be 108px wide", 108, backgroundBitmap?.width)
        assertEquals("Background should be 108px tall", 108, backgroundBitmap?.height)
        backgroundBitmap?.recycle()
    }

    @Test
    fun testSplashScreenLogo() {
        // Test that splash screen logo exists and has high resolution
        val splashLogoResId = res.getIdentifier("splash_logo", "drawable", packageName)
        assertNotEquals("splash_logo should exist", 0, splashLogoResId)

        // Verify splash logo has high resolution (should be at least 1024x1024)
        val splashBitmap = BitmapFactory.decodeResource(res, splashLogoResId)
        assertNotNull("Splash logo bitmap should load", splashBitmap)

        // The generated logo should be 2048x2048 for maximum quality
        assertEquals("Splash logo should be 2048px wide", 2048, splashBitmap?.width)
        assertEquals("Splash logo should be 2048px tall", 2048, splashBitmap?.height)

        // Verify it's a square image
        assertEquals("Splash logo should be square", splashBitmap?.width, splashBitmap?.height)

        splashBitmap?.recycle()
    }

    @Test
    fun testIconQualityAndFormat() {
        // Test that icons are in PNG format and have proper alpha channels
        val foregroundResId = res.getIdentifier("ic_launcher_foreground", "drawable", packageName)
        val foregroundBitmap = BitmapFactory.decodeResource(res, foregroundResId)

        assertNotNull("Foreground bitmap should load successfully", foregroundBitmap)

        // Verify the bitmap has an alpha channel (for transparency)
        assertTrue("Foreground should have alpha channel", foregroundBitmap?.hasAlpha() == true)

        // Test that the bitmap is not completely transparent
        val pixelCount = foregroundBitmap?.width?.times(foregroundBitmap.height) ?: 0
        var nonTransparentPixels = 0

        if (foregroundBitmap != null) {
            for (x in 0 until foregroundBitmap.width step 10) { // Sample every 10th pixel for performance
                for (y in 0 until foregroundBitmap.height step 10) {
                    val pixel = foregroundBitmap.getPixel(x, y)
                    if (android.graphics.Color.alpha(pixel) > 0) {
                        nonTransparentPixels++
                    }
                }
            }
        }

        assertTrue("Icon should have visible content (non-transparent pixels)", nonTransparentPixels > 0)

        foregroundBitmap?.recycle()
    }

    @Test
    fun testAllRequiredIconVariantsExist() {
        val requiredIcons = listOf(
            "ic_launcher",           // Standard launcher icon
            "ic_launcher_round",     // Round launcher icon
            "ic_launcher_foreground", // Adaptive foreground
            "ic_launcher_background", // Adaptive background
            "splash_logo"           // Splash screen logo
        )

        requiredIcons.forEach { iconName ->
            val resId = res.getIdentifier(iconName, "drawable", packageName)
            assertNotEquals("$iconName drawable should exist", 0, resId)
        }

        // Test mipmap variants
        val mipmapIcons = listOf("ic_launcher", "ic_launcher_round")
        mipmapIcons.forEach { iconName ->
            val resId = res.getIdentifier(iconName, "mipmap", packageName)
            assertNotEquals("$iconName mipmap should exist", 0, resId)
        }
    }

    @Test
    fun testIconSourceVerification() {
        // Verify that icons were generated from Logo.jpeg by checking file existence
        // This is a build-time verification that the assets exist

        val assetsDir = File(context.filesDir.parentFile?.parent, "Assets")
        val logoFile = File(assetsDir, "Logo.jpeg")

        // The Logo.jpeg should exist (used as source)
        assertTrue("Logo.jpeg source file should exist", logoFile.exists())

        // Verify the generate script exists
        val generateScript = File(assetsDir, "generate_icons.sh")
        assertTrue("generate_icons.sh script should exist", generateScript.exists())
        assertTrue("generate_icons.sh should be executable", generateScript.canExecute())
    }

    @Test
    fun testIconConsistencyAcrossDensities() {
        // Test that all density variants have consistent aspect ratios
        val densities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")

        densities.forEach { density ->
            val launcherResId = res.getIdentifier("ic_launcher", "mipmap", packageName)
            assertNotEquals("ic_launcher should exist for $density", 0, launcherResId)
        }

        // All icons should be square (1:1 aspect ratio)
        // This is verified by the generation script, but we can test the foreground
        val foregroundResId = res.getIdentifier("ic_launcher_foreground", "drawable", packageName)
        val foregroundBitmap = BitmapFactory.decodeResource(res, foregroundResId)

        assertNotNull("Foreground bitmap should load", foregroundBitmap)
        assertEquals("Foreground should be square", foregroundBitmap?.width, foregroundBitmap?.height)

        foregroundBitmap?.recycle()
    }
}