package com.shareconnect.homeassistantconnect.data.api

import com.shareconnect.homeassistantconnect.data.models.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.homeassistantconnect.TestApplication::class)
class HomeAssistantApiClientMockKTest {

    private lateinit var mockService: HomeAssistantApiService
    private lateinit var apiClient: HomeAssistantApiClient

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = HomeAssistantApiClient(
            serverUrl = "http://test.local:8123",
            accessToken = "test-token-123",
            homeAssistantApiService = mockService
        )
    }

    @Test
    fun `test getApiStatus success`() = runBlocking {
        val mockStatus = HomeAssistantApiStatus(message = "API running.")
        coEvery { mockService.getApiStatus(any()) } returns Response.success(mockStatus)
        val result = apiClient.getApiStatus()
        assertTrue(result.isSuccess)
        assertEquals("API running.", result.getOrNull()?.message)
        coVerify { mockService.getApiStatus(any()) }
    }

    @Test
    fun `test getConfig success`() = runBlocking {
        val mockConfig = HomeAssistantConfig(
            latitude = 32.87336, longitude = -117.22743, elevation = 123,
            unitSystem = UnitSystem("km", "g", "°C", "L"),
            locationName = "Home", timeZone = "America/Los_Angeles",
            components = listOf("http", "mqtt"), configDir = "/config",
            whitelistExternalDirs = emptyList(), allowlistExternalDirs = emptyList(),
            version = "2023.10.1", configSource = "storage", safeMode = false,
            state = "RUNNING", externalUrl = null, internalUrl = null
        )
        coEvery { mockService.getConfig(any()) } returns Response.success(mockConfig)
        val result = apiClient.getConfig()
        assertTrue(result.isSuccess)
        assertEquals("Home", result.getOrNull()?.locationName)
        coVerify { mockService.getConfig(any()) }
    }

    @Test
    fun `test getEvents success`() = runBlocking {
        val mockEvents = listOf(HomeAssistantEvent("state_changed", 150))
        coEvery { mockService.getEvents(any()) } returns Response.success(mockEvents)
        val result = apiClient.getEvents()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify { mockService.getEvents(any()) }
    }

    @Test
    fun `test getServices success`() = runBlocking {
        val mockServices = listOf(HomeAssistantServiceDomain("light", mapOf()))
        coEvery { mockService.getServices(any()) } returns Response.success(mockServices)
        val result = apiClient.getServices()
        assertTrue(result.isSuccess)
        assertEquals("light", result.getOrNull()?.get(0)?.domain)
        coVerify { mockService.getServices(any()) }
    }

    @Test
    fun `test getStates success`() = runBlocking {
        val mockStates = listOf(HomeAssistantState("light.living_room", "on", mapOf(), "2023-10-01T12:00:00", "2023-10-01T12:00:00", null))
        coEvery { mockService.getStates(any()) } returns Response.success(mockStates)
        val result = apiClient.getStates()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify { mockService.getStates(any()) }
    }

    @Test
    fun `test getState success`() = runBlocking {
        val mockState = HomeAssistantState("sensor.temp", "22.5", mapOf(), "2023-10-01T12:00:00", "2023-10-01T12:00:00", null)
        coEvery { mockService.getState(any(), any()) } returns Response.success(mockState)
        val result = apiClient.getState("sensor.temp")
        assertTrue(result.isSuccess)
        assertEquals("22.5", result.getOrNull()?.state)
        coVerify { mockService.getState("sensor.temp", any()) }
    }

    @Test
    fun `test setState success`() = runBlocking {
        val newState = HomeAssistantState("input_boolean.test", "on", mapOf(), null, null, null)
        coEvery { mockService.setState(any(), any(), any()) } returns Response.success(newState)
        val result = apiClient.setState("input_boolean.test", newState)
        assertTrue(result.isSuccess)
        coVerify { mockService.setState(any(), any(), any()) }
    }

    @Test
    fun `test callService success`() = runBlocking {
        val request = ServiceCallRequest(entityId = "light.bedroom")
        val mockResponse = listOf(ServiceCallResponse(StateContext("ctx1", null, null)))
        coEvery { mockService.callService(any(), any(), any(), any()) } returns Response.success(mockResponse)
        val result = apiClient.callService("light", "turn_on", request)
        assertTrue(result.isSuccess)
        coVerify { mockService.callService(any(), any(), any(), any()) }
    }

    @Test
    fun `test fireEvent success`() = runBlocking {
        val mockResponse = ServiceCallResponse(StateContext("ctx2", null, null))
        coEvery { mockService.fireEvent(any(), any(), any()) } returns Response.success(mockResponse)
        val result = apiClient.fireEvent("custom_event", mapOf())
        assertTrue(result.isSuccess)
        coVerify { mockService.fireEvent(any(), any(), any()) }
    }

    @Test
    fun `test getErrorLog success`() = runBlocking {
        coEvery { mockService.getErrorLog(any()) } returns Response.success("ERROR: test")
        val result = apiClient.getErrorLog()
        assertTrue(result.isSuccess)
        coVerify { mockService.getErrorLog(any()) }
    }

    @Test
    fun `test getCameraImage success`() = runBlocking {
        val mockImage = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
        coEvery { mockService.getCameraImage(any(), any()) } returns Response.success(mockImage)
        val result = apiClient.getCameraImage("camera.front")
        assertTrue(result.isSuccess)
        coVerify { mockService.getCameraImage(any(), any()) }
    }

    @Test
    fun `test getHistory success`() = runBlocking {
        val mockHistory = listOf(listOf(HomeAssistantState("sensor.temp", "22", mapOf(), "2023-10-01T10:00:00", "2023-10-01T10:00:00", null)))
        coEvery { mockService.getHistory(any(), any(), any(), any(), any(), any(), any()) } returns Response.success(mockHistory)
        val result = apiClient.getHistory("2023-10-01T00:00:00Z")
        assertTrue(result.isSuccess)
        coVerify { mockService.getHistory(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `test getLogbook success`() = runBlocking {
        val mockLogbook = listOf(HomeAssistantLogbookEntry("2023-10-01T12:00:00", "Light", "turned on", "light", "light.living_room", "ctx1", "user1", null))
        coEvery { mockService.getLogbook(any(), any(), any(), any()) } returns Response.success(mockLogbook)
        val result = apiClient.getLogbook("2023-10-01T00:00:00Z")
        assertTrue(result.isSuccess)
        coVerify { mockService.getLogbook(any(), any(), any(), any()) }
    }

    @Test
    fun `test renderTemplate success`() = runBlocking {
        coEvery { mockService.renderTemplate(any(), any()) } returns Response.success("Temp is 22°C")
        val result = apiClient.renderTemplate("{{ states('sensor.temp') }}")
        assertTrue(result.isSuccess)
        coVerify { mockService.renderTemplate(any(), any()) }
    }

    @Test
    fun `test handleIntent success`() = runBlocking {
        val request = IntentRequest("GetTemp", mapOf())
        val mockResponse = IntentResponse(mapOf(), mapOf(), mapOf())
        coEvery { mockService.handleIntent(any(), any()) } returns Response.success(mockResponse)
        val result = apiClient.handleIntent(request)
        assertTrue(result.isSuccess)
        coVerify { mockService.handleIntent(any(), any()) }
    }

    @Test
    fun `test getCalendarEvents success`() = runBlocking {
        val mockEvents = listOf(CalendarEvent(EventTime("2023-10-15T10:00:00", null), EventTime("2023-10-15T11:00:00", null), "Meeting", null, null, "event1", null, null))
        coEvery { mockService.getCalendarEvents(any(), any(), any(), any()) } returns Response.success(mockEvents)
        val result = apiClient.getCalendarEvents("calendar.work")
        assertTrue(result.isSuccess)
        coVerify { mockService.getCalendarEvents(any(), any(), any(), any()) }
    }

    @Test
    fun `test HTTP 401 error`() = runBlocking {
        coEvery { mockService.getApiStatus(any()) } returns Response.error(401, mockk(relaxed = true))
        val result = apiClient.getApiStatus()
        assertTrue(result.isFailure)
    }

    @Test
    fun `test HTTP 404 error`() = runBlocking {
        coEvery { mockService.getState(any(), any()) } returns Response.error(404, mockk(relaxed = true))
        val result = apiClient.getState("light.none")
        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getConfig(any()) } throws Exception("Network error")
        val result = apiClient.getConfig()
        assertTrue(result.isFailure)
    }

    @Test
    fun `test callService exception`() = runBlocking {
        coEvery { mockService.callService(any(), any(), any(), any()) } throws Exception("Timeout")
        val result = apiClient.callService("light", "turn_on", ServiceCallRequest())
        assertTrue(result.isFailure)
    }
}
