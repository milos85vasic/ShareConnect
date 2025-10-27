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


package com.redelf.commons.test

import com.redelf.commons.application.BaseApplication
import com.redelf.commons.authentification.Credentials
import com.redelf.commons.extensions.CountDownLatch
import com.redelf.commons.logging.Console
import com.redelf.commons.management.Management
import com.redelf.commons.management.managers.ManagersInitializer
import org.junit.Assert
import org.junit.Before
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

abstract class ManagersDependentTest : BaseTest() {

    protected open val managers: List<Management> = listOf()

    /**
     * Credentials set to be used for testing
     * @return List of pairs: credentials to be used for testing vs expected auth. result
     */
    open val credentialsSet: List<Pair<Credentials, Boolean>> = listOf()

    protected open fun setup() {

        Console.initialize(failOnError = true)

        Console.log("Timber initialized: $this")

        BaseApplication.DEBUG.set(true)

        setupStorage()
        setupManagers()
    }

    private fun setupStorage() {

        Console.log("Timber initialized: $this")
    }

    private fun setupManagers() {

        val registered = AtomicInteger()
        val setupSuccess = AtomicBoolean()
        val mainLatch = CountDownLatch(1, "test")
        val latch = CountDownLatch(managers.size, "test")

        val managersInitializerCallback = object : ManagersInitializer.InitializationCallback {

            override fun onInitialization(

                manager: Management,
                success: Boolean,
                error: Throwable?

            ) {

                Assert.assertTrue(success)
                Assert.assertNull(error)

                if (success) {

                    registered.incrementAndGet()
                }

                latch.countDown()
            }

            override fun onInitialization(success: Boolean, error: Throwable?) {

                setupSuccess.set(success)

                mainLatch.countDown()
            }
        }

        ManagersInitializer().initializeManagers(

            managers,
            managersInitializerCallback,
            context = TestApplication.takeContext(),
        )

        latch.await()
        mainLatch.await()

        Assert.assertTrue(setupSuccess.get())
        Assert.assertEquals(managers.size, registered.get())
    }

    @Before
    fun doSetup() = setup()
}