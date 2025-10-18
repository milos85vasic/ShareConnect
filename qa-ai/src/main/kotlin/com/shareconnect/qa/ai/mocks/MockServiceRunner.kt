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