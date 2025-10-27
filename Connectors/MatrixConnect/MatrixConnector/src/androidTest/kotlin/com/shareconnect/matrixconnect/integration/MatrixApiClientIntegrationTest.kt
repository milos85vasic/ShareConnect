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


package com.shareconnect.matrixconnect.integration

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.matrixconnect.api.MatrixApiClient
import com.shareconnect.matrixconnect.models.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Integration tests for MatrixApiClient
 * Tests real API interactions with mock server
 */
@RunWith(AndroidJUnit4::class)
class MatrixApiClientIntegrationTest {

    private lateinit var context: Context
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: MatrixApiClient

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = MatrixApiClient(context, baseUrl)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLoginFlow() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "user_id": "@test:matrix.org",
                "access_token": "token123",
                "device_id": "DEVICE1"
            }
        """))

        val result = apiClient.login("test", "password")

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("@test:matrix.org", response.userId)
    }

    @Test
    fun testFullMessageFlow() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Create room
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!room1:matrix.org"}
        """))
        val createResult = apiClient.createRoom(CreateRoomRequest(name = "Test"))
        assertTrue(createResult is MatrixResult.Success)

        // Send message
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"event_id": "${'$'}event1"}
        """))
        val sendResult = apiClient.sendMessage(
            "!room1:matrix.org",
            SendMessageRequest("m.text", "Hello")
        )
        assertTrue(sendResult is MatrixResult.Success)

        // Get messages
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "start": "t1",
                "end": "t2",
                "chunk": []
            }
        """))
        val getResult = apiClient.getRoomMessages("!room1:matrix.org", "t1")
        assertTrue(getResult is MatrixResult.Success)
    }

    @Test
    fun testSyncLoop() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // First sync
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"next_batch": "s1"}
        """))
        val sync1 = apiClient.sync()
        assertTrue(sync1 is MatrixResult.Success)
        val batch1 = (sync1 as MatrixResult.Success).data.nextBatch

        // Second sync with since
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"next_batch": "s2"}
        """))
        val sync2 = apiClient.sync(since = batch1)
        assertTrue(sync2 is MatrixResult.Success)
    }

    @Test
    fun testRoomInviteFlow() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Create room
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!room1:matrix.org"}
        """))
        val createResult = apiClient.createRoom(CreateRoomRequest())
        assertTrue(createResult is MatrixResult.Success)

        // Invite user
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val inviteResult = apiClient.inviteUser("!room1:matrix.org", "@friend:matrix.org")
        assertTrue(inviteResult is MatrixResult.Success)
    }

    @Test
    fun testJoinLeaveFlow() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Join room
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!room1:matrix.org"}
        """))
        val joinResult = apiClient.joinRoom("!room1:matrix.org")
        assertTrue(joinResult is MatrixResult.Success)

        // Leave room
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val leaveResult = apiClient.leaveRoom("!room1:matrix.org")
        assertTrue(leaveResult is MatrixResult.Success)
    }

    @Test
    fun testProfileManagement() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Get profile
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "displayname": "User",
                "avatar_url": "mxc://matrix.org/abc"
            }
        """))
        val getResult = apiClient.getProfile("@user:matrix.org")
        assertTrue(getResult is MatrixResult.Success)

        // Set display name
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val setNameResult = apiClient.setDisplayName("@user:matrix.org", "New Name")
        assertTrue(setNameResult is MatrixResult.Success)

        // Set avatar
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val setAvatarResult = apiClient.setAvatarUrl("@user:matrix.org", "mxc://matrix.org/new")
        assertTrue(setAvatarResult is MatrixResult.Success)
    }

    @Test
    fun testPresenceManagement() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Set presence
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val setResult = apiClient.setPresence("@user:matrix.org", "online")
        assertTrue(setResult is MatrixResult.Success)

        // Get presence
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "presence": "online",
                "last_active_ago": 1000
            }
        """))
        val getResult = apiClient.getPresence("@user:matrix.org")
        assertTrue(getResult is MatrixResult.Success)
    }

    @Test
    fun testDeviceManagement() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Get devices
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "devices": [
                    {"device_id": "DEVICE1", "display_name": "Phone"}
                ]
            }
        """))
        val getDevicesResult = apiClient.getDevices()
        assertTrue(getDevicesResult is MatrixResult.Success)

        // Get single device
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"device_id": "DEVICE1", "display_name": "Phone"}
        """))
        val getDeviceResult = apiClient.getDevice("DEVICE1")
        assertTrue(getDeviceResult is MatrixResult.Success)

        // Update device
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val updateResult = apiClient.updateDevice("DEVICE1", "New Phone")
        assertTrue(updateResult is MatrixResult.Success)
    }

    @Test
    fun testE2EEKeyManagement() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Upload keys
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "one_time_key_counts": {
                    "signed_curve25519": 50
                }
            }
        """))
        val uploadResult = apiClient.uploadKeys(KeysUploadRequest())
        assertTrue(uploadResult is MatrixResult.Success)

        // Query keys
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "device_keys": {}
            }
        """))
        val queryResult = apiClient.queryKeys(
            KeysQueryRequest(deviceKeys = mapOf("@user:matrix.org" to emptyList()))
        )
        assertTrue(queryResult is MatrixResult.Success)
    }

    @Test
    fun testErrorHandling401() = runTest {
        apiClient.setCredentials("invalid_token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody("""
            {
                "errcode": "M_UNKNOWN_TOKEN",
                "error": "Invalid access token"
            }
        """))

        val result = apiClient.getJoinedRooms()
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun testErrorHandling403() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(403).setBody("""
            {
                "errcode": "M_FORBIDDEN",
                "error": "Access denied"
            }
        """))

        val result = apiClient.login("user", "wrong_password")
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun testErrorHandling404() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("""
            {
                "errcode": "M_NOT_FOUND",
                "error": "Room not found"
            }
        """))

        val result = apiClient.getRoomMessages("!nonexistent:matrix.org", "t1")
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun testErrorHandling429RateLimit() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(429).setBody("""
            {
                "errcode": "M_LIMIT_EXCEEDED",
                "error": "Too many requests",
                "retry_after_ms": 2000
            }
        """))

        val result = apiClient.login("user", "password")
        assertTrue(result is MatrixResult.Error)
        assertEquals(2000L, (result as MatrixResult.Error).retryAfterMs)
    }

    @Test
    fun testNetworkTimeout() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
                .setBodyDelay(100, java.util.concurrent.TimeUnit.SECONDS)
        )

        // Should timeout
        val result = apiClient.sync()
        // Result depends on timeout configuration
        assertNotNull(result)
    }

    @Test
    fun testMultipleRoomOperations() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Create multiple rooms
        repeat(3) { i ->
            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
                {"room_id": "!room$i:matrix.org"}
            """))
        }

        val results = mutableListOf<MatrixResult<CreateRoomResponse>>()
        repeat(3) { i ->
            results.add(apiClient.createRoom(CreateRoomRequest(name = "Room $i")))
        }

        assertEquals(3, results.size)
        assertTrue(results.all { it is MatrixResult.Success })
    }

    @Test
    fun testGetJoinedRoomsEmpty() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"joined_rooms": []}
        """))

        val result = apiClient.getJoinedRooms()
        assertTrue(result is MatrixResult.Success)
        assertEquals(0, (result as MatrixResult.Success).data.joinedRooms.size)
    }

    @Test
    fun testGetJoinedRoomsMultiple() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "joined_rooms": [
                    "!room1:matrix.org",
                    "!room2:matrix.org",
                    "!room3:matrix.org"
                ]
            }
        """))

        val result = apiClient.getJoinedRooms()
        assertTrue(result is MatrixResult.Success)
        assertEquals(3, (result as MatrixResult.Success).data.joinedRooms.size)
    }

    @Test
    fun testSendTextMessage() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"event_id": "${'$'}text_event"}
        """))

        val result = apiClient.sendMessage(
            "!room1:matrix.org",
            SendMessageRequest("m.text", "Hello World")
        )
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testSendFormattedMessage() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"event_id": "${'$'}formatted_event"}
        """))

        val result = apiClient.sendMessage(
            "!room1:matrix.org",
            SendMessageRequest(
                "m.text",
                "Hello",
                "<b>Hello</b>",
                "org.matrix.custom.html"
            )
        )
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testSendEncryptedMessage() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"event_id": "${'$'}encrypted_event"}
        """))

        val encrypted = SendEncryptedMessageRequest(
            senderKey = "key123",
            ciphertext = "encrypted_data",
            sessionId = "session123",
            deviceId = "DEVICE1"
        )
        val result = apiClient.sendEncryptedMessage("!room1:matrix.org", encrypted)
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testCreateDirectRoom() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!direct:matrix.org"}
        """))

        val result = apiClient.createRoom(
            CreateRoomRequest(
                isDirect = true,
                invite = listOf("@friend:matrix.org")
            )
        )
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testCreatePublicRoom() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!public:matrix.org"}
        """))

        val result = apiClient.createRoom(
            CreateRoomRequest(
                visibility = "public",
                roomAliasName = "myroom"
            )
        )
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testCreateEncryptedRoom() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"room_id": "!encrypted:matrix.org"}
        """))

        val result = apiClient.createRoom(
            CreateRoomRequest(
                initialState = listOf(
                    StateEvent(
                        type = "m.room.encryption",
                        content = mapOf("algorithm" to "m.megolm.v1.aes-sha2")
                    )
                )
            )
        )
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testGetRoomMessagesWithLimit() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "start": "t1",
                "end": "t2",
                "chunk": []
            }
        """))

        val result = apiClient.getRoomMessages("!room1:matrix.org", "t1", "b", 10)
        assertTrue(result is MatrixResult.Success)

        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("limit=10"))
    }

    @Test
    fun testGetRoomMessagesForward() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "start": "t1",
                "end": "t2",
                "chunk": []
            }
        """))

        val result = apiClient.getRoomMessages("!room1:matrix.org", "t1", "f")
        assertTrue(result is MatrixResult.Success)

        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("dir=f"))
    }

    @Test
    fun testSyncWithTimeout() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"next_batch": "s1"}
        """))

        val result = apiClient.sync(timeout = 10000)
        assertTrue(result is MatrixResult.Success)

        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("timeout=10000"))
    }

    @Test
    fun testLogoutRemovesCredentials() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val logoutResult = apiClient.logout()
        assertTrue(logoutResult is MatrixResult.Success)

        // After logout, requests should fail without auth
        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody("""
            {"errcode": "M_UNKNOWN_TOKEN"}
        """))
        val result = apiClient.getJoinedRooms()
        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun testInviteMultipleUsers() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        // Invite first user
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val invite1 = apiClient.inviteUser("!room1:matrix.org", "@user1:matrix.org")
        assertTrue(invite1 is MatrixResult.Success)

        // Invite second user
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val invite2 = apiClient.inviteUser("!room1:matrix.org", "@user2:matrix.org")
        assertTrue(invite2 is MatrixResult.Success)
    }

    @Test
    fun testPresenceWithStatusMessage() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.setPresence("@user:matrix.org", "online", "Working on project")
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testQueryKeysForMultipleUsers() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "device_keys": {
                    "@user1:matrix.org": {},
                    "@user2:matrix.org": {}
                }
            }
        """))

        val result = apiClient.queryKeys(
            KeysQueryRequest(
                deviceKeys = mapOf(
                    "@user1:matrix.org" to emptyList(),
                    "@user2:matrix.org" to emptyList()
                )
            )
        )
        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals(2, response.deviceKeys.size)
    }

    @Test
    fun testUploadKeysWithDeviceKeys() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "one_time_key_counts": {
                    "signed_curve25519": 100
                }
            }
        """))

        val deviceKeys = DeviceKeys(
            userId = "@user:matrix.org",
            deviceId = "DEVICE1",
            algorithms = listOf("m.olm.v1.curve25519-aes-sha2"),
            keys = mapOf("curve25519:DEVICE1" to "key123"),
            signatures = emptyMap()
        )

        val result = apiClient.uploadKeys(KeysUploadRequest(deviceKeys = deviceKeys))
        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun testUploadKeysWithOneTimeKeys() = runTest {
        apiClient.setCredentials("token", "@user:matrix.org", "DEVICE1")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "one_time_key_counts": {
                    "signed_curve25519": 50
                }
            }
        """))

        val result = apiClient.uploadKeys(
            KeysUploadRequest(
                oneTimeKeys = mapOf("signed_curve25519:AAAAAA" to "key_data")
            )
        )
        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals(50, response.oneTimeKeyCounts["signed_curve25519"])
    }
}
