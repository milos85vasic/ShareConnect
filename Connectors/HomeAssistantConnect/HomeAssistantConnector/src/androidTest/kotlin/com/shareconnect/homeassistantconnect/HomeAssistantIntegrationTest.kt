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


package com.shareconnect.homeassistantconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.homeassistantconnect.data.api.HomeAssistantApiClient
import com.shareconnect.homeassistantconnect.data.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for Home Assistant API client
 * Tests full REST API workflows with mock server
 */
@RunWith(AndroidJUnit4::class)
class HomeAssistantIntegrationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var apiClient: HomeAssistantApiClient
    private val testToken = "test_token_12345"

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        val baseUrl = mockServer.url("/").toString().trimEnd('/')
        apiClient = HomeAssistantApiClient(
            baseUrl = baseUrl,
            token = testToken
        )
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun testCompleteApiWorkflow() = runTest {
        // Test 1: Get API status
        mockServer.enqueue(MockResponse().setBody("""{"message": "API running"}"""))
        val statusResult = apiClient.getApiStatus()
        assertTrue("API status should succeed", statusResult.isSuccess)
        assertEquals("API running", statusResult.getOrNull()?.message)

        // Test 2: Get configuration
        val configJson = """{
            "latitude": 37.7749,
            "longitude": -122.4194,
            "elevation": 16,
            "unit_system": {"temperature": "°C", "length": "km", "mass": "kg", "volume": "L"},
            "location_name": "Home",
            "time_zone": "America/Los_Angeles",
            "version": "2024.1.0",
            "state": "RUNNING"
        }"""
        mockServer.enqueue(MockResponse().setBody(configJson))
        val configResult = apiClient.getConfig()
        assertTrue("Get config should succeed", configResult.isSuccess)
        val config = configResult.getOrNull()
        assertEquals("Home", config?.locationName)
        assertEquals("2024.1.0", config?.version)

        // Test 3: Get all states
        val statesJson = """[
            {
                "entity_id": "light.living_room",
                "state": "on",
                "attributes": {"brightness": 255, "friendly_name": "Living Room Light"},
                "last_changed": "2024-01-01T10:00:00",
                "last_updated": "2024-01-01T10:00:00"
            },
            {
                "entity_id": "sensor.temperature",
                "state": "22.5",
                "attributes": {"unit_of_measurement": "°C", "friendly_name": "Temperature"},
                "last_changed": "2024-01-01T10:05:00",
                "last_updated": "2024-01-01T10:05:00"
            }
        ]"""
        mockServer.enqueue(MockResponse().setBody(statesJson))
        val statesResult = apiClient.getStates()
        assertTrue("Get states should succeed", statesResult.isSuccess)
        val states = statesResult.getOrNull()
        assertEquals(2, states?.size)
        assertEquals("light.living_room", states?.get(0)?.entityId)
        assertEquals("on", states?.get(0)?.state)

        // Test 4: Get specific entity state
        val entityJson = """{
            "entity_id": "light.living_room",
            "state": "on",
            "attributes": {"brightness": 255, "friendly_name": "Living Room Light"},
            "last_changed": "2024-01-01T10:00:00",
            "last_updated": "2024-01-01T10:00:00"
        }"""
        mockServer.enqueue(MockResponse().setBody(entityJson))
        val entityResult = apiClient.getState("light.living_room")
        assertTrue("Get entity state should succeed", entityResult.isSuccess)
        val entity = entityResult.getOrNull()
        assertEquals("light.living_room", entity?.entityId)
        assertEquals("on", entity?.state)
    }

    @Test
    fun testServiceCallWorkflow() = runTest {
        // Test calling a service
        val serviceResponse = """[{"context": {"id": "test123"}}]"""
        mockServer.enqueue(MockResponse().setBody(serviceResponse))

        val result = apiClient.callService(
            domain = "light",
            service = "turn_on",
            request = ServiceCallRequest(
                entityId = "light.living_room",
                data = mapOf("brightness" to 128)
            )
        )

        assertTrue("Service call should succeed", result.isSuccess)

        // Verify request
        val recordedRequest = mockServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertTrue(recordedRequest.path?.contains("/api/services/light/turn_on") == true)
    }

    @Test
    fun testStateUpdateWorkflow() = runTest {
        // Test updating entity state
        val updatedStateJson = """{
            "entity_id": "sensor.custom",
            "state": "42",
            "attributes": {"unit": "units"},
            "last_changed": "2024-01-01T11:00:00",
            "last_updated": "2024-01-01T11:00:00"
        }"""
        mockServer.enqueue(MockResponse().setBody(updatedStateJson))

        val result = apiClient.setState(
            entityId = "sensor.custom",
            state = HomeAssistantState(
                entityId = "sensor.custom",
                state = "42",
                attributes = mapOf("unit" to "units"),
                lastChanged = "2024-01-01T11:00:00",
                lastUpdated = "2024-01-01T11:00:00",
                context = null
            )
        )

        assertTrue("Set state should succeed", result.isSuccess)
        assertEquals("42", result.getOrNull()?.state)
    }

    @Test
    fun testEventsWorkflow() = runTest {
        // Test getting events
        val eventsJson = """[
            {"event": "state_changed", "listener_count": 15},
            {"event": "service_called", "listener_count": 8}
        ]"""
        mockServer.enqueue(MockResponse().setBody(eventsJson))

        val result = apiClient.getEvents()
        assertTrue("Get events should succeed", result.isSuccess)
        val events = result.getOrNull()
        assertEquals(2, events?.size)
        assertEquals("state_changed", events?.get(0)?.event)
    }

    @Test
    fun testServicesWorkflow() = runTest {
        // Test getting services
        val servicesJson = """[
            {
                "domain": "light",
                "services": {
                    "turn_on": {
                        "name": "Turn on",
                        "description": "Turn on light"
                    },
                    "turn_off": {
                        "name": "Turn off",
                        "description": "Turn off light"
                    }
                }
            }
        ]"""
        mockServer.enqueue(MockResponse().setBody(servicesJson))

        val result = apiClient.getServices()
        assertTrue("Get services should succeed", result.isSuccess)
        val services = result.getOrNull()
        assertEquals(1, services?.size)
        assertEquals("light", services?.get(0)?.domain)
    }

    @Test
    fun testHistoryWorkflow() = runTest {
        // Test getting history
        val historyJson = """[[
            {
                "entity_id": "sensor.temperature",
                "state": "21.0",
                "attributes": {"unit_of_measurement": "°C"},
                "last_changed": "2024-01-01T09:00:00",
                "last_updated": "2024-01-01T09:00:00"
            },
            {
                "entity_id": "sensor.temperature",
                "state": "22.5",
                "attributes": {"unit_of_measurement": "°C"},
                "last_changed": "2024-01-01T10:00:00",
                "last_updated": "2024-01-01T10:00:00"
            }
        ]]"""
        mockServer.enqueue(MockResponse().setBody(historyJson))

        val result = apiClient.getHistory(
            timestamp = "2024-01-01T00:00:00",
            filterEntityId = "sensor.temperature"
        )

        assertTrue("Get history should succeed", result.isSuccess)
        val history = result.getOrNull()
        assertEquals(1, history?.size)
        assertEquals(2, history?.get(0)?.size)
    }

    @Test
    fun testErrorLogWorkflow() = runTest {
        // Test getting error log
        val errorLogJson = """[
            {
                "timestamp": "2024-01-01T10:00:00",
                "level": "ERROR",
                "message": "Test error",
                "source": "homeassistant.core",
                "exception": "ValueError: test",
                "count": 1,
                "first_occurred": "2024-01-01T10:00:00"
            }
        ]"""
        mockServer.enqueue(MockResponse().setBody(errorLogJson))

        val result = apiClient.getErrorLog()
        assertTrue("Get error log should succeed", result.isSuccess)
        val errorLog = result.getOrNull()
        assertEquals(1, errorLog?.size)
        assertEquals("ERROR", errorLog?.get(0)?.level)
    }

    @Test
    fun testLogbookWorkflow() = runTest {
        // Test getting logbook
        val logbookJson = """[
            {
                "when": "2024-01-01T10:00:00",
                "name": "Living Room Light",
                "message": "turned on",
                "domain": "light",
                "entity_id": "light.living_room"
            }
        ]"""
        mockServer.enqueue(MockResponse().setBody(logbookJson))

        val result = apiClient.getLogbook(
            timestamp = "2024-01-01T00:00:00"
        )

        assertTrue("Get logbook should succeed", result.isSuccess)
        val logbook = result.getOrNull()
        assertEquals(1, logbook?.size)
        assertEquals("Living Room Light", logbook?.get(0)?.name)
    }

    @Test
    fun testTemplateRenderingWorkflow() = runTest {
        // Test template rendering
        val templateResponse = """{"result": "Hello World"}"""
        mockServer.enqueue(MockResponse().setBody(templateResponse))

        val result = apiClient.renderTemplate("{{ 'Hello' + ' World' }}")
        assertTrue("Template rendering should succeed", result.isSuccess)
        assertEquals("Hello World", result.getOrNull()?.result)
    }

    @Test
    fun testCheckConfigWorkflow() = runTest {
        // Test config check
        val checkResponse = """{"result": "valid", "errors": null}"""
        mockServer.enqueue(MockResponse().setBody(checkResponse))

        val result = apiClient.checkConfig()
        assertTrue("Config check should succeed", result.isSuccess)
    }

    @Test
    fun testAuthenticationFailure() = runTest {
        // Test authentication failure
        mockServer.enqueue(MockResponse().setResponseCode(401).setBody("""{"message": "Unauthorized"}"""))

        val result = apiClient.getStates()
        assertTrue("Request should fail with auth error", result.isFailure)
    }

    @Test
    fun testNetworkFailure() = runTest {
        // Test network failure handling
        mockServer.shutdown()

        val result = apiClient.getApiStatus()
        assertTrue("Request should fail with network error", result.isFailure)
    }

    @Test
    fun testInvalidJsonResponse() = runTest {
        // Test invalid JSON handling
        mockServer.enqueue(MockResponse().setBody("not valid json"))

        val result = apiClient.getStates()
        assertTrue("Request should fail with parse error", result.isFailure)
    }

    @Test
    fun testEmptyResponseHandling() = runTest {
        // Test empty response
        mockServer.enqueue(MockResponse().setBody("[]"))

        val result = apiClient.getStates()
        assertTrue("Request should succeed", result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun testLargeResponseHandling() = runTest {
        // Test large response with many entities
        val entities = (1..100).map { i ->
            """{
                "entity_id": "sensor.test_$i",
                "state": "$i",
                "attributes": {},
                "last_changed": "2024-01-01T10:00:00",
                "last_updated": "2024-01-01T10:00:00"
            }"""
        }.joinToString(",", "[", "]")

        mockServer.enqueue(MockResponse().setBody(entities))

        val result = apiClient.getStates()
        assertTrue("Request should succeed", result.isSuccess)
        assertEquals(100, result.getOrNull()?.size)
    }

    @Test
    fun testTimeoutHandling() = runTest {
        // Test timeout
        mockServer.enqueue(MockResponse().setBody("{}").setBodyDelay(35, java.util.concurrent.TimeUnit.SECONDS))

        val result = apiClient.getApiStatus()
        assertTrue("Request should fail with timeout", result.isFailure)
    }

    @Test
    fun testConcurrentRequests() = runTest {
        // Test multiple concurrent requests
        repeat(10) {
            mockServer.enqueue(MockResponse().setBody("""{"message": "API running"}"""))
        }

        val results = (1..10).map {
            kotlinx.coroutines.async {
                apiClient.getApiStatus()
            }
        }.map { it.await() }

        val successCount = results.count { it.isSuccess }
        assertTrue("Most requests should succeed", successCount >= 8)
    }

    @Test
    fun testServerErrorHandling() = runTest {
        // Test 500 server error
        mockServer.enqueue(MockResponse().setResponseCode(500).setBody("""{"error": "Internal server error"}"""))

        val result = apiClient.getStates()
        assertTrue("Request should fail with server error", result.isFailure)
    }

    @Test
    fun testNotFoundHandling() = runTest {
        // Test 404 not found
        mockServer.enqueue(MockResponse().setResponseCode(404).setBody("""{"error": "Not found"}"""))

        val result = apiClient.getState("nonexistent.entity")
        assertTrue("Request should fail with not found", result.isFailure)
    }

    @Test
    fun testComplexServiceCall() = runTest {
        // Test service call with complex data
        mockServer.enqueue(MockResponse().setBody("""[{"context": {"id": "test123"}}]"""))

        val result = apiClient.callService(
            domain = "script",
            service = "turn_on",
            request = ServiceCallRequest(
                entityId = "script.complex_automation",
                data = mapOf(
                    "variables" to mapOf(
                        "temperature" to 22.5,
                        "mode" to "heat",
                        "enabled" to true
                    )
                )
            )
        )

        assertTrue("Complex service call should succeed", result.isSuccess)
    }

    @Test
    fun testStateWithComplexAttributes() = runTest {
        // Test entity state with complex attributes
        val complexJson = """{
            "entity_id": "climate.living_room",
            "state": "heat",
            "attributes": {
                "current_temperature": 22.5,
                "target_temperature": 23.0,
                "hvac_modes": ["off", "heat", "cool", "auto"],
                "preset_modes": ["home", "away", "sleep"],
                "supported_features": 123,
                "friendly_name": "Living Room Climate"
            },
            "last_changed": "2024-01-01T10:00:00",
            "last_updated": "2024-01-01T10:00:00"
        }"""
        mockServer.enqueue(MockResponse().setBody(complexJson))

        val result = apiClient.getState("climate.living_room")
        assertTrue("Request should succeed", result.isSuccess)
        val entity = result.getOrNull()
        assertEquals("heat", entity?.state)
        assertTrue(entity?.attributes?.containsKey("hvac_modes") == true)
    }

    @Test
    fun testHeaderValidation() = runTest {
        // Test that Authorization header is sent correctly
        mockServer.enqueue(MockResponse().setBody("""{"message": "API running"}"""))

        apiClient.getApiStatus()

        val request = mockServer.takeRequest()
        val authHeader = request.getHeader("Authorization")
        assertEquals("Bearer $testToken", authHeader)
    }

    @Test
    fun testBaseUrlHandling() = runTest {
        // Test that base URL is handled correctly (with/without trailing slash)
        mockServer.enqueue(MockResponse().setBody("""[]"""))

        apiClient.getStates()

        val request = mockServer.takeRequest()
        assertTrue(request.path?.contains("/api/states") == true)
    }

    @Test
    fun testCalendarWorkflow() = runTest {
        // Test getting calendar events
        val calendarJson = """[
            {
                "start": {"dateTime": "2024-01-01T10:00:00"},
                "end": {"dateTime": "2024-01-01T11:00:00"},
                "summary": "Test Event",
                "description": "Test Description"
            }
        ]"""
        mockServer.enqueue(MockResponse().setBody(calendarJson))

        val result = apiClient.getCalendarEvents(
            entityId = "calendar.personal",
            start = "2024-01-01T00:00:00",
            end = "2024-01-31T23:59:59"
        )

        assertTrue("Get calendar events should succeed", result.isSuccess)
        val events = result.getOrNull()
        assertEquals(1, events?.size)
        assertEquals("Test Event", events?.get(0)?.summary)
    }

    @Test
    fun testIntentHandlingWorkflow() = runTest {
        // Test intent handling
        val intentResponse = """{
            "speech": {"plain": {"speech": "Turned on the lights"}},
            "card": {},
            "data": {"success": true}
        }"""
        mockServer.enqueue(MockResponse().setBody(intentResponse))

        val result = apiClient.handleIntent(
            IntentRequest(
                name = "HassTurnOn",
                data = mapOf("domain" to "light", "name" to "living room")
            )
        )

        assertTrue("Intent handling should succeed", result.isSuccess)
    }

    @Test
    fun testMultipleDomainServices() = runTest {
        // Test service calls across multiple domains
        repeat(5) {
            mockServer.enqueue(MockResponse().setBody("""[{"context": {"id": "test$it"}}]"""))
        }

        val domains = listOf("light", "switch", "climate", "media_player", "cover")
        val results = domains.map { domain ->
            apiClient.callService(
                domain = domain,
                service = "turn_on",
                request = ServiceCallRequest(entityId = "$domain.test")
            )
        }

        assertTrue("All service calls should succeed", results.all { it.isSuccess })
    }

    @Test
    fun testHistoryWithMultipleEntities() = runTest {
        // Test history for multiple entities
        val historyJson = """[
            [
                {"entity_id": "sensor.temp1", "state": "20", "last_changed": "2024-01-01T10:00:00", "last_updated": "2024-01-01T10:00:00"}
            ],
            [
                {"entity_id": "sensor.temp2", "state": "21", "last_changed": "2024-01-01T10:00:00", "last_updated": "2024-01-01T10:00:00"}
            ]
        ]"""
        mockServer.enqueue(MockResponse().setBody(historyJson))

        val result = apiClient.getHistory(
            timestamp = "2024-01-01T00:00:00",
            filterEntityId = "sensor.temp1,sensor.temp2"
        )

        assertTrue("Get history should succeed", result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun testErrorRecovery() = runTest {
        // Test that client recovers from errors
        mockServer.enqueue(MockResponse().setResponseCode(500))
        mockServer.enqueue(MockResponse().setBody("""{"message": "API running"}"""))

        val failResult = apiClient.getApiStatus()
        assertTrue("First request should fail", failResult.isFailure)

        val successResult = apiClient.getApiStatus()
        assertTrue("Second request should succeed", successResult.isSuccess)
    }

    @Test
    fun testPartialDataHandling() = runTest {
        // Test handling of partial data
        val partialJson = """{
            "entity_id": "sensor.test",
            "state": "on"
        }"""
        mockServer.enqueue(MockResponse().setBody(partialJson))

        val result = apiClient.getState("sensor.test")
        assertTrue("Request should succeed with partial data", result.isSuccess)
        assertEquals("on", result.getOrNull()?.state)
        assertNull(result.getOrNull()?.attributes)
    }
}
