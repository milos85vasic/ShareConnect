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


package com.shareconnect.automation

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.shareconnect.MainActivity
import com.shareconnect.R
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/**
 * UI Automation test that captures screenshots of language selection flow
 *
 * This test navigates through the language selection process and captures screenshots
 * demonstrating the language synchronization feature across the app.
 *
 * Screenshots are saved to: /sdcard/Pictures/ShareConnect/
 */
@RunWith(AndroidJUnit4::class)
class LanguageUIAutomationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private var screenshotCounter = 1

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()

        // Create screenshot directory
        val screenshotDir = File("/sdcard/Pictures/ShareConnect")
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }
    }

    @Test
    fun testLanguageSelectionFlow_CaptureScreenshots() {
        // Allow app to initialize
        Thread.sleep(2000)

        // Screenshot 1: Main Activity
        takeScreenshot("01_main_activity")
        Thread.sleep(500)

        // Open navigation drawer (if exists) or menu
        try {
            onView(withContentDescription("Open navigation drawer"))
                .perform(click())
            Thread.sleep(500)
            takeScreenshot("02_navigation_drawer")
        } catch (e: Exception) {
            // No navigation drawer, continue
        }

        // Navigate to Settings
        try {
            // Try finding settings in action bar or options menu
            device.pressMenu()
            Thread.sleep(500)
            takeScreenshot("03_menu_opened")
        } catch (e: Exception) {
            // Continue
        }

        // Close any open drawers/menus and open settings via navigation
        pressBack()
        Thread.sleep(500)

        // Open Settings directly using the settings button or menu
        try {
            onView(withId(R.id.action_settings)).perform(click())
            Thread.sleep(1000)
            takeScreenshot("04_settings_activity")
        } catch (e: Exception) {
            // Settings might be accessed differently
            takeScreenshot("04_alternate_navigation")
        }

        // Click on Language Selection preference
        try {
            onView(allOf(withId(android.R.id.title), withText("Language")))
                .perform(click())
            Thread.sleep(1000)
            takeScreenshot("05_language_selection_activity")
        } catch (e: Exception) {
            // Try alternative language selection access
            takeScreenshot("05_language_selection_error")
        }

        // Capture screenshot of language list
        Thread.sleep(500)
        takeScreenshot("06_language_list")

        // Select English (if not already selected)
        try {
            onView(withText("English"))
                .perform(click())
            Thread.sleep(1500)
            takeScreenshot("07_english_selected")
        } catch (e: Exception) {
            // Continue
        }

        // Navigate back to settings
        pressBack()
        Thread.sleep(500)

        // Open language selection again
        try {
            onView(allOf(withId(android.R.id.title), withText("Language")))
                .perform(click())
            Thread.sleep(1000)
        } catch (e: Exception) {
            // Continue
        }

        // Select Spanish
        try {
            onView(withText("Español"))
                .perform(click())
            Thread.sleep(1500)
            takeScreenshot("08_spanish_selected")
        } catch (e: Exception) {
            takeScreenshot("08_spanish_selection_error")
        }

        // Navigate back to see Spanish in UI
        pressBack()
        Thread.sleep(500)
        takeScreenshot("09_settings_in_spanish")

        // Open language selection one more time
        try {
            pressBack()
            Thread.sleep(500)
            device.pressMenu()
            Thread.sleep(500)
            onView(withId(R.id.action_settings)).perform(click())
            Thread.sleep(1000)
            onView(allOf(withId(android.R.id.title), withText("Idioma"))) // Spanish for "Language"
                .perform(click())
            Thread.sleep(1000)
            takeScreenshot("10_language_list_in_spanish")
        } catch (e: Exception) {
            takeScreenshot("10_language_navigation_error")
        }

        // Select French
        try {
            onView(withText("Français"))
                .perform(click())
            Thread.sleep(1500)
            takeScreenshot("11_french_selected")
        } catch (e: Exception) {
            takeScreenshot("11_french_selection_error")
        }

        // Navigate back to see French in UI
        pressBack()
        Thread.sleep(500)
        takeScreenshot("12_settings_in_french")

        // Navigate back to main activity
        pressBack()
        Thread.sleep(500)
        takeScreenshot("13_main_activity_in_french")

        // Reset to English
        try {
            device.pressMenu()
            Thread.sleep(500)
            onView(withId(R.id.action_settings)).perform(click())
            Thread.sleep(1000)
            onView(allOf(withId(android.R.id.title), withText("Langue"))) // French for "Language"
                .perform(click())
            Thread.sleep(1000)
            onView(withText("English"))
                .perform(click())
            Thread.sleep(1500)
            takeScreenshot("14_reset_to_english")
        } catch (e: Exception) {
            takeScreenshot("14_reset_error")
        }

        // Final screenshot
        pressBack()
        pressBack()
        Thread.sleep(500)
        takeScreenshot("15_final_main_activity")
    }

    /**
     * Capture screenshot using UiAutomator
     */
    private fun takeScreenshot(name: String) {
        try {
            val screenshotFile = File("/sdcard/Pictures/ShareConnect", "${screenshotCounter.toString().padStart(2, '0')}_${name}.png")
            val success = device.takeScreenshot(screenshotFile)

            if (success) {
                println("Screenshot saved: ${screenshotFile.absolutePath}")
                screenshotCounter++
            } else {
                println("Failed to save screenshot: $name")
            }
        } catch (e: Exception) {
            println("Error taking screenshot $name: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Alternative screenshot method using window decorView
     * (Kept for reference, UiAutomator method is preferred)
     */
    private fun takeScreenshotAlternative(name: String) {
        activityRule.scenario.onActivity { activity ->
            try {
                val view = activity.window.decorView.rootView
                view.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(view.drawingCache)
                view.isDrawingCacheEnabled = false

                val screenshotFile = File("/sdcard/Pictures/ShareConnect", "${screenshotCounter.toString().padStart(2, '0')}_${name}.png")
                FileOutputStream(screenshotFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                println("Screenshot saved: ${screenshotFile.absolutePath}")
                screenshotCounter++
            } catch (e: Exception) {
                println("Error taking screenshot $name: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
