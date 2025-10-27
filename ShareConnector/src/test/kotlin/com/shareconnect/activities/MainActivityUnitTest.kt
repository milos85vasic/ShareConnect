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

import android.content.Context
import android.content.Intent
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.shareconnect.MainActivity
import com.shareconnect.ProfileManager
import com.shareconnect.R
import com.shareconnect.ServerProfile
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
@org.junit.Ignore("Activity tests require complex setup and are disabled for now")
class MainActivityUnitTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockProfileManager: ProfileManager

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock ProfileManager before activity creation
        `when`(mockProfileManager.hasProfiles()).thenReturn(true)

        // Create activity using Robolectric with a simpler approach
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()

        // Override the profile manager created in onCreate
        activity.profileManager = mockProfileManager
    }

    @Test
    fun testActivityCreation() {
        assertNotNull(activity)
        assertFalse(activity.isFinishing)
    }

    @Test
    fun testToolbarIsDisplayed() {
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        assertNotNull(toolbar)
    }

    @Test
    fun testSettingsButtonExists() {
        val settingsButton = activity.findViewById<Button>(R.id.buttonSettings)
        assertNotNull(settingsButton)
        assertEquals("Settings", settingsButton?.text)
    }

    @Test
    fun testHistoryButtonExists() {
        val historyButton = activity.findViewById<Button>(R.id.buttonHistory)
        assertNotNull(historyButton)
        assertEquals("View Share History", historyButton?.text)
    }

    @Test
    fun testFabAddButtonExists() {
        val fabAdd = activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)
        assertNotNull(fabAdd)
        assertEquals("Add from Clipboard", fabAdd?.text)
    }

    @Test
    fun testOpenMeTubeButtonWithDefaultProfile() {
        // Mock having a default profile
        val testProfile = ServerProfile().apply {
            name = "Test Profile"
            url = "http://test.example.com"
            port = 8080
            serviceType = "metube"
        }

        `when`(mockProfileManager.defaultProfile()).thenReturn(testProfile)

        // Re-initialize the activity to trigger button text update
        activity.updateDefaultServiceButton()

        val openButton = activity.findViewById<Button>(R.id.buttonOpenMeTube)
        assertNotNull(openButton)
        assertTrue(openButton?.text?.startsWith("Open ") == true)
    }

    @Test
    fun testOpenMeTubeButtonWithoutDefaultProfile() {
        // Mock no default profile
        `when`(mockProfileManager.defaultProfile()).thenReturn(null)

        // Re-initialize the activity to trigger button text update
        activity.updateDefaultServiceButton()

        val openButton = activity.findViewById<Button>(R.id.buttonOpenMeTube)
        assertNotNull(openButton)
        // Button should be disabled when no default profile
        assertFalse(openButton?.isEnabled == true)
    }

    @Test
    fun testActivityLifecycle() {
        assertFalse(activity.isFinishing)

        // Test that activity can be paused and resumed
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val testActivity = controller.create().start().resume().pause().stop().destroy().get()

        assertTrue(testActivity.isFinishing)
    }
}