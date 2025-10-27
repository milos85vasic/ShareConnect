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


package com.shareconnect.utorrentconnect.e2e.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

//region View is displayed
fun assertViewDisplayed(matcher: Matcher<View>) {
    onView(matcher).let {
        try {
            it.perform(scrollTo())
        } catch (e: PerformException) {
            it
        }
    }.check(matches(isDisplayed()))
}

fun assertViewWithIdDisplayed(@IdRes id: Int) = assertViewDisplayed(withId(id))

fun assertViewWithTextDisplayed(text: String) = assertViewDisplayed(withText(text))

fun assertViewOfTypeDisplayed(type: Class<out View>) = assertViewDisplayed(isAssignableFrom(type))
//endregion

//region View is hidden
fun assertViewHidden(matcher: Matcher<View>) {
    try {
        onView(matcher).check(matches(not(isDisplayed())))
    } catch (e: NoMatchingViewException) {
        onView(matcher).check(doesNotExist())
    }
}

fun assertViewWithIdHidden(@IdRes id: Int) = assertViewHidden(allOf(withId(id)))

fun assertViewOfTypeHidden(type: Class<out View>) = assertViewHidden(allOf(isAssignableFrom(type)))
//endregion

//region View exists
fun assertViewExists(matcher: Matcher<View>) {
    onView(matcher).check(matches(not(doesNotExist())))
}

fun assertViewWithIdExists(@IdRes id: Int) = assertViewExists(withId(id))
//endregion

//region RecyclerView
fun recyclerViewHasItemCount(matcher: Matcher<View>, expectedCount: Int) {
    onView(matcher).check(RecyclerViewItemCountAssertion(expectedCount))
}

fun recyclerViewWithIdHasItemCount(@IdRes id: Int, expectedCount: Int) {
    recyclerViewHasItemCount(withId(id), expectedCount)
}
//endregion

//region RecyclerView
fun assertViewWithIdHasText(@IdRes id: Int, text: String) {
    onView(withId(id)).check(matches(withText(text)))
}
//endregion
