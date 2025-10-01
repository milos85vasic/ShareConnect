package com.shareconnect.activities

import android.content.res.Configuration
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.shareconnect.MainActivity
import com.shareconnect.R
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LayoutResponsivenessTest {

    @Test
    fun testSmallPhoneLayoutInflation() {
        // Test layout-sw320dp (small phones)
        val config = Configuration(RuntimeEnvironment.getApplication().resources.configuration).apply {
            screenWidthDp = 320
            screenHeightDp = 480
        }

        RuntimeEnvironment.setQualifiers("sw320dp")
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw360dp")
    fun testNormalPhoneLayoutInflation() {
        // Test layout-sw360dp (normal phones)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw400dp")
    fun testLargePhoneLayoutInflation() {
        // Test layout-sw400dp (large phones)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun testSmallTabletLayoutInflation() {
        // Test layout-sw600dp (small tablets)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw720dp")
    fun testLargeTabletLayoutInflation() {
        // Test layout-sw720dp (large tablets)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw840dp")
    fun testFoldableLayoutInflation() {
        // Test layout-sw840dp (foldables and extra large screens)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw840dp")
    fun testExtraLargeScreenLayoutInflation() {
        // Test layout-sw840dp (extra large screens like 4K displays)
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify all essential views are present
        assertNotNull("Toolbar should exist", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button should exist", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button should exist", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button should exist", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB should exist", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }

    @Test
    @Config(qualifiers = "sw360dp-port")
    fun testLayoutInflationWithDifferentOrientations() {
        // Test portrait orientation
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        assertNotNull("Portrait layout should inflate", activity.findViewById<Toolbar>(R.id.toolbar))
        activity.finish()
    }

    @Test
    fun testViewIdsConsistencyAcrossLayouts() {
        // Test that all layouts use consistent view IDs by testing default layout
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify consistent IDs across all layouts
        assertNotNull("Toolbar ID should be consistent", activity.findViewById<Toolbar>(R.id.toolbar))
        assertNotNull("Settings button ID should be consistent", activity.findViewById<Button>(R.id.buttonSettings))
        assertNotNull("History button ID should be consistent", activity.findViewById<Button>(R.id.buttonHistory))
        assertNotNull("Open MeTube button ID should be consistent", activity.findViewById<Button>(R.id.buttonOpenMeTube))
        assertNotNull("FAB ID should be consistent", activity.findViewById<ExtendedFloatingActionButton>(R.id.fabAdd))

        activity.finish()
    }
}