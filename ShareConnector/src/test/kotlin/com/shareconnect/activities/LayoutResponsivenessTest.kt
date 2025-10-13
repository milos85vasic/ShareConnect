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
@Config(sdk = [33], application = android.app.Application::class)
class LayoutResponsivenessTest {

    @Test
    @Config(sdk = [33], qualifiers = "sw320dp")
    fun testSmallPhoneLayoutInflation() {
        // Test layout-sw320dp (small phones)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw360dp")
    fun testNormalPhoneLayoutInflation() {
        // Test layout-sw360dp (normal phones)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw400dp")
    fun testLargePhoneLayoutInflation() {
        // Test layout-sw400dp (large phones)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw600dp")
    fun testSmallTabletLayoutInflation() {
        // Test layout-sw600dp (small tablets)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw720dp")
    fun testLargeTabletLayoutInflation() {
        // Test layout-sw720dp (large tablets)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw840dp")
    fun testFoldableLayoutInflation() {
        // Test layout-sw840dp (foldables and extra large screens)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw840dp")
    fun testExtraLargeScreenLayoutInflation() {
        // Test layout-sw840dp (extra large screens like 4K displays)
        val context = RuntimeEnvironment.getApplication()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_main, null)

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", view.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", view.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", view.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", view.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", view.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))
    }

    @Test
    @Config(sdk = [33], qualifiers = "sw360dp-port")
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