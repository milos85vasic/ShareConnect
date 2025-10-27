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


package com.shareconnect.suites

import com.shareconnect.api.ServiceApiClientTest
import com.shareconnect.database.HistoryItemTest
import com.shareconnect.database.ThemeTest
import com.shareconnect.manager.ProfileManagerTest
import com.shareconnect.model.ServerProfileTest
import com.shareconnect.utils.DialogUtilsTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Unit Test Suite for ShareConnect Application
 *
 * This suite runs all unit tests that don't require Android context
 * or can run with Robolectric.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ServerProfileTest::class,
    ProfileManagerTest::class,
    ThemeTest::class,
    HistoryItemTest::class,
    DialogUtilsTest::class,
    ServiceApiClientTest::class
)
class UnitTestSuite