package com.shareconnect.plexconnect.api

import com.shareconnect.plexconnect.data.api.PlexApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlexApiClientIntegrationTest {

    private val apiClient = PlexApiClient(isStubMode = true)

    @Test
    fun `request PIN generates valid PIN response`() = runBlocking {
        val clientId = "test-client-id"
        val pinResult = apiClient.requestPin(clientId)

        assertTrue("PIN request should succeed", pinResult.isSuccess)
        val pinResponse = pinResult.getOrNull()
        
        assertNotNull("PIN response should not be null", pinResponse)
        assertNotNull("PIN ID should be present", pinResponse?.id)
        assertNotNull("PIN code should be present", pinResponse?.code)
    }

    @Test
    fun `check non-existent PIN fails gracefully`() = runBlocking {
        val nonExistentPinId = 999999L
        val checkResult = apiClient.checkPin(nonExistentPinId)

        assertTrue("PIN check for non-existent PIN should fail", checkResult.isFailure)
    }

    @Test
    fun `get server info with valid URL succeeds`() = runBlocking {
        // Note: This test uses stub mode, so it should succeed
        val serverUrl = "http://localhost:32400"
        val serverInfoResult = apiClient.getServerInfo(serverUrl)

        // In stub mode, this should succeed with test data
        assertTrue("Server info request should succeed in stub mode", serverInfoResult.isSuccess)
        
        val serverInfo = serverInfoResult.getOrNull()
        assertNotNull("Server info should not be null", serverInfo)
        assertNotNull("Server name should be present", serverInfo?.name)
    }

    companion object {
        // Configuration for actual integration testing would go here
        private const val TEST_PLEX_TOKEN = ""
        private const val TEST_PLEX_SERVER_URL = ""
    }
}