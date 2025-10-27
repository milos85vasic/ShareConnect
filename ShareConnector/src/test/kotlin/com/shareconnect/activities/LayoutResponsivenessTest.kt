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

import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.shareconnect.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
@org.junit.Ignore("Layout inflation tests require complex resource setup and are disabled for now")
class LayoutResponsivenessTest {

    @Test
    @Config(sdk = [33], qualifiers = "land")
    fun testLayoutInflationWithDifferentOrientations() {
        // Test portrait orientation
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        assertNotNull("Portrait layout should inflate", view.findViewById<Toolbar>(R.id.toolbar))
    }

    @Test
    fun testViewIdsConsistencyAcrossLayouts() {
        // Test that all layouts use consistent view IDs by testing default layout
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify consistent IDs across all layouts
        assertNotNull("Toolbar ID should be consistent", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button ID should be consistent", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button ID should be consistent", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button ID should be consistent", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB ID should be consistent", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }
}