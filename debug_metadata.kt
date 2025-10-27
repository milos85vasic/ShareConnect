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


import android.content.Context
import org.mockito.Mockito.mock

// Simple test to debug MetadataFetcher
fun main() {
    val mockContext = mock(Context::class.java)
    val fetcher = MetadataFetcher(mockContext)

    val magnetUrl = "magnet:?xt=urn:btih:fedcba9876543210&xl=2048000000"
    val metadata = runBlocking { fetcher.fetchMetadata(magnetUrl) }

    println("Title: ${metadata.title}")
    println("Description: ${metadata.description}")
    println("SiteName: ${metadata.siteName}")

    // Check what the test expects
    println("\nTest expectations:")
    println("Title should be: 'Magnet Link'")
    println("Description should contain: '1.9 GB'")
    println("Description should contain: 'fedcba98...'")
    println("SiteName should be: 'BitTorrent'")
}