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

import com.redelf.commons.data.list.HttpStringsListDataSource
import com.redelf.commons.net.proxy.http.HttpProxies
import com.redelf.commons.obtain.Obtain
import org.junit.Assert
import java.util.concurrent.atomic.AtomicLong

abstract class ProxiesTest : EndpointsTest() {

    protected open fun testHttpSourceProxies(sourceAddress: String) {

        try {

            val urlObtain = object : Obtain<String> {

                override fun obtain() = sourceAddress
            }

            val source = HttpStringsListDataSource(urlObtain)
            var proxies = HttpProxies(applicationContext, sources = listOf(source), alive = false)
            var obtained = proxies.obtain()

            Assert.assertNotNull(obtained)
            Assert.assertTrue(obtained.isNotEmpty())

            proxies = HttpProxies(applicationContext, sources = listOf(source), alive = true)
            obtained = proxies.obtain()

            Assert.assertNotNull(obtained)

            val iterator = obtained.iterator()
            val quality = AtomicLong(Long.MAX_VALUE)

            while (iterator.hasNext()) {

                val proxy = iterator.next()

                Assert.assertNotNull(proxy)
                Assert.assertTrue(proxy.address.isNotBlank())
                Assert.assertTrue(proxy.port > 0)
                Assert.assertTrue(proxy.isAlive(applicationContext))

                val newQuality = proxy.getQuality()

                Assert.assertTrue(newQuality < quality.get())

                quality.set(newQuality)
            }

        } catch (e: Throwable) {

            Assert.fail(e.message)
        }
    }
}