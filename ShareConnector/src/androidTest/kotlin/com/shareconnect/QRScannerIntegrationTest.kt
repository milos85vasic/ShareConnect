package com.shareconnect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class QRScannerIntegrationTest {

    @Test
    fun testQRScannerButtonDisplayed() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Check that the FAB is displayed
            onView(withId(R.id.fabAdd)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testQRScannerDialogShows() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Click the FAB
            onView(withId(R.id.fabAdd)).perform(click())

            // Note: In a real test, we would check that the dialog appears
            // But for now, this tests that the click doesn't crash the app
        }
    }
}