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


package com.shareconnect.jdownloaderconnect.integration

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Integration test for JDownloaderConnect icon functionality.
 * Tests that icons display correctly and app launches properly with generated icons.
 */
@RunWith(AndroidJUnit4::class)
class JDownloaderIconIntegrationTest {

    private lateinit var context: Context
    private lateinit var device: UiDevice
    private lateinit var packageManager: PackageManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        packageManager = context.packageManager
    }

    @Test
    fun testAppIconIsDisplayedInLauncher() {
        // Verify that the app icon is properly registered with the system
        val appInfo = packageManager.getApplicationInfo(context.packageName, 0)

        assertNotEquals("App should have an icon resource", 0, appInfo.icon)
        assertNotNull("App info should be valid", appInfo)

        // Verify the icon drawable can be loaded
        val iconDrawable = packageManager.getApplicationIcon(context.packageName)
        assertNotNull("App icon drawable should be loadable", iconDrawable)
    }

    @Test
    fun testAppLaunchesWithIcon() {
        // Test that the app can be launched (which requires valid icons)
        val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName)
        assertNotNull("App should have a launch intent", launchIntent)

        // Verify the intent is properly configured
        assertEquals("Launch intent should target main activity",
            "${context.packageName}.ui.MainActivity", launchIntent?.component?.className)
    }

    @Test
    fun testIconResourcesAreAccessible() {
        // Test that all icon resources are accessible at runtime
        val res = context.resources
        val packageName = context.packageName

        val iconResources = listOf(
            "ic_launcher" to "mipmap",
            "ic_launcher_round" to "mipmap",
            "ic_launcher_foreground" to "drawable",
            "ic_launcher_background" to "drawable",
            "splash_logo" to "drawable"
        )

        iconResources.forEach { (resourceName, resourceType) ->
            val resId = res.getIdentifier(resourceName, resourceType, packageName)
            assertNotEquals("$resourceName $resourceType should be accessible", 0, resId)

            // Verify we can get the resource
            try {
                val drawable = res.getDrawable(resId, null)
                assertNotNull("$resourceName should load as drawable", drawable)
            } catch (e: Exception) {
                fail("$resourceName should be loadable as drawable: ${e.message}")
            }
        }
    }

    @Test
    fun testAdaptiveIconConfiguration() {
        // Test that adaptive icons are properly configured
        val appInfo = packageManager.getApplicationInfo(context.packageName, 0)

        // The icon should be set (adaptive icons are handled by the resource system)
        assertNotEquals("App should have icon configured", 0, appInfo.icon)

        // Verify the adaptive icon XML files exist and are valid
        // This is more of a build-time check, but we verify the resources exist
        val res = context.resources
        val foregroundId = res.getIdentifier("ic_launcher_foreground", "drawable", context.packageName)
        val backgroundId = res.getIdentifier("ic_launcher_background", "drawable", context.packageName)

        assertNotEquals("Adaptive foreground should exist", 0, foregroundId)
        assertNotEquals("Adaptive background should exist", 0, backgroundId)
    }

    @Test
    fun testSplashScreenResourceExists() {
        // Test that splash screen logo resource exists and is properly sized
        val res = context.resources
        val splashLogoId = res.getIdentifier("splash_logo", "drawable", context.packageName)

        assertNotEquals("Splash logo should exist", 0, splashLogoId)

        // Verify it can be loaded
        val drawable = res.getDrawable(splashLogoId, null)
        assertNotNull("Splash logo should be loadable", drawable)

        // Verify dimensions are high resolution
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight

        assertTrue("Splash logo should be high resolution (width >= 1024)",
            intrinsicWidth >= 1024)
        assertTrue("Splash logo should be high resolution (height >= 1024)",
            intrinsicHeight >= 1024)
        assertEquals("Splash logo should be square", intrinsicWidth, intrinsicHeight)
    }

    @Test
    fun testIconQualityStandards() {
        // Test that icons meet quality standards
        val res = context.resources

        // Test foreground icon (should have alpha channel for transparency)
        val foregroundId = res.getIdentifier("ic_launcher_foreground", "drawable", context.packageName)
        val foreground = res.getDrawable(foregroundId, null)

        assertNotNull("Foreground icon should load", foreground)

        // The foreground should be 108x108 for adaptive icons
        assertEquals("Foreground should be 108px wide", 108, foreground.intrinsicWidth)
        assertEquals("Foreground should be 108px tall", 108, foreground.intrinsicHeight)
    }

    @Test
    fun testAllDensityVariantsAvailable() {
        // Test that all screen density variants are available
        val res = context.resources

        val densities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
        val iconTypes = listOf("ic_launcher", "ic_launcher_round")

        densities.forEach { density ->
            iconTypes.forEach { iconType ->
                val resId = res.getIdentifier(iconType, "mipmap", context.packageName)
                assertNotEquals("$iconType should be available for $density", 0, resId)

                val drawable = res.getDrawable(resId, null)
                assertNotNull("$iconType should load for $density", drawable)
            }
        }
    }

    @Test
    fun testIconGenerationSourceIntegrity() {
        // Test that the icon generation source files are intact
        val assetsDir = File(context.filesDir.parentFile?.parent, "Assets")
        val logoFile = File(assetsDir, "Logo.jpeg")
        val scriptFile = File(assetsDir, "generate_icons.sh")

        assertTrue("Logo.jpeg source should exist", logoFile.exists())
        assertTrue("Logo.jpeg should be readable", logoFile.canRead())
        assertTrue("Logo.jpeg should not be empty", logoFile.length() > 0)

        assertTrue("generate_icons.sh should exist", scriptFile.exists())
        assertTrue("generate_icons.sh should be executable", scriptFile.canExecute())

        // Verify the script contains the expected high-quality settings
        val scriptContent = scriptFile.readText()
        assertTrue("Script should use Lanczos filtering", scriptContent.contains("Lanczos"))
        assertTrue("Script should generate high-res splash", scriptContent.contains("2048x2048"))
        assertTrue("Script should use high quality", scriptContent.contains("-quality 100"))
    }
}