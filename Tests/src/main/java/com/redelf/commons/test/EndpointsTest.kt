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

import com.redelf.commons.logging.Console
import com.redelf.commons.net.endpoint.http.HttpEndpoint
import com.redelf.commons.net.endpoint.http.HttpEndpoints
import org.junit.Assert
import java.util.concurrent.atomic.AtomicLong

abstract class EndpointsTest : BaseTest() {

    protected open fun getAndTestDefaultEndpoints() {

        try {

            var endpoints = HttpEndpoints(applicationContext, alive = false)
            var obtained = endpoints.obtain()

            Assert.assertNotNull(obtained)
            Assert.assertTrue(obtained.isNotEmpty())

            endpoints = HttpEndpoints(applicationContext, alive = true)
            obtained = endpoints.obtain()

            Assert.assertNotNull(obtained)

            val iterator = obtained.iterator()
            val quality = AtomicLong(Long.MAX_VALUE)

            while (iterator.hasNext()) {

                val endpoint = iterator.next()

                Assert.assertNotNull(endpoint)
                Assert.assertTrue(endpoint.address.isNotBlank())
                Assert.assertTrue(endpoint.isAlive(applicationContext))

                val newQuality = endpoint.getQuality()

                Assert.assertTrue(newQuality < quality.get())

                quality.set(newQuality)

                onEndpoint(endpoint)
            }

        } catch (e: Throwable) {

            Assert.fail(e.message)
        }
    }

    protected open fun onEndpoint(endpoint: HttpEndpoint) {

        Console.log("Endpoint :: $endpoint")
    }
}