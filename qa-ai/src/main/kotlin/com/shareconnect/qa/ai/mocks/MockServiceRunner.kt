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


package com.shareconnect.qa.ai.mocks

import okhttp3.mockwebserver.MockWebServer
import java.util.concurrent.ConcurrentHashMap

/**
 * Command-line runner for mock services
 * Can be executed with: java -cp mock-services.jar com.shareconnect.qa.ai.mocks.MockServiceRunner [services...]
 */
object MockServiceRunner {

    private val servers = ConcurrentHashMap<String, MockWebServer>()

    @JvmStatic
    fun main(args: Array<String>) {
        val servicesToStart = if (args.isEmpty() || args[0] == "all") {
            MockServiceManager.SERVICE_CONFIGS.keys.toList()
        } else {
            args.toList()
        }

        println("Starting mock services: ${servicesToStart.joinToString(", ")}")

        val serviceManager = MockServiceManager()
        val serviceUrls = serviceManager.startAllServices()

        println("Mock services started successfully!")
        serviceUrls.forEach { (service, url) ->
            println("  $service: $url")
        }

        // Keep running until interrupted
        Runtime.getRuntime().addShutdownHook(Thread {
            println("\nShutting down mock services...")
            serviceManager.stopAllServices()
            println("Mock services stopped.")
        })

        // Wait indefinitely
        try {
            Thread.currentThread().join()
        } catch (e: InterruptedException) {
            // Shutdown requested
        }
    }
}