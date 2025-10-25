package com.shareconnect.matrixconnect

import android.content.Context
import com.shareconnect.matrixconnect.api.MatrixApiClient
import com.shareconnect.matrixconnect.models.*
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MatrixApiClient
 * Tests all Matrix Client-Server API endpoints
 */
class MatrixApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: MatrixApiClient
    private lateinit var context: Context

    @Before
    fun setup() {
        context = mockk(relaxed = true)
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
    fun `test login success returns access token`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "user_id": "@user:matrix.org",
                    "access_token": "test_token_123",
                    "device_id": "DEVICE_123"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.login("user", "password")

        assertTrue(result is MatrixResult.Success)
        val loginResponse = (result as MatrixResult.Success).data
        assertEquals("@user:matrix.org", loginResponse.userId)
        assertEquals("test_token_123", loginResponse.accessToken)
        assertEquals("DEVICE_123", loginResponse.deviceId)
    }

    @Test
    fun `test login failure returns error`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(403)
            .setBody("""
                {
                    "errcode": "M_FORBIDDEN",
                    "error": "Invalid password"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.login("user", "wrong_password")

        assertTrue(result is MatrixResult.Error)
        val error = result as MatrixResult.Error
        assertEquals("M_FORBIDDEN", error.code)
        assertEquals("Invalid password", error.message)
    }

    @Test
    fun `test sync returns next batch token`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "next_batch": "s123_456_789",
                    "rooms": {
                        "join": {}
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.sync()

        assertTrue(result is MatrixResult.Success)
        val syncResponse = (result as MatrixResult.Success).data
        assertEquals("s123_456_789", syncResponse.nextBatch)
    }

    @Test
    fun `test sync with since parameter`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "next_batch": "s123_456_790"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.sync(since = "s123_456_789")

        assertTrue(result is MatrixResult.Success)
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("since=s123_456_789"))
    }

    @Test
    fun `test createRoom success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "room_id": "!abc123:matrix.org"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = CreateRoomRequest(name = "Test Room")
        val result = apiClient.createRoom(request)

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("!abc123:matrix.org", response.roomId)
    }

    @Test
    fun `test getJoinedRooms returns room list`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "joined_rooms": [
                        "!room1:matrix.org",
                        "!room2:matrix.org"
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getJoinedRooms()

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals(2, response.joinedRooms.size)
        assertTrue(response.joinedRooms.contains("!room1:matrix.org"))
    }

    @Test
    fun `test getRoomMessages returns events`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "start": "t1",
                    "end": "t2",
                    "chunk": [
                        {
                            "type": "m.room.message",
                            "sender": "@user:matrix.org",
                            "origin_server_ts": 1234567890,
                            "content": {
                                "msgtype": "m.text",
                                "body": "Hello"
                            }
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getRoomMessages("!room1:matrix.org", "t1")

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("t1", response.start)
        assertEquals(1, response.chunk.size)
    }

    @Test
    fun `test sendMessage success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "event_id": "${'$'}event123"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val message = SendMessageRequest(msgtype = "m.text", body = "Hello")
        val result = apiClient.sendMessage("!room1:matrix.org", message)

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("\$event123", response.eventId)
    }

    @Test
    fun `test sendMessage validates authorization`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"event_id":"${'$'}e1"}"""))

        val message = SendMessageRequest(msgtype = "m.text", body = "Test")
        apiClient.sendMessage("!room1:matrix.org", message)

        val request = mockWebServer.takeRequest()
        assertEquals("Bearer test_token", request.getHeader("Authorization"))
    }

    @Test
    fun `test joinRoom success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "room_id": "!room1:matrix.org"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.joinRoom("!room1:matrix.org")

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("!room1:matrix.org", response["room_id"])
    }

    @Test
    fun `test leaveRoom success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.leaveRoom("!room1:matrix.org")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test inviteUser success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.inviteUser("!room1:matrix.org", "@friend:matrix.org")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test getProfile returns user info`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "displayname": "Test User",
                    "avatar_url": "mxc://matrix.org/abc123"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getProfile("@user:matrix.org")

        assertTrue(result is MatrixResult.Success)
        val profile = (result as MatrixResult.Success).data
        assertEquals("Test User", profile.displayname)
        assertEquals("mxc://matrix.org/abc123", profile.avatarUrl)
    }

    @Test
    fun `test setDisplayName success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.setDisplayName("@user:matrix.org", "New Name")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test setAvatarUrl success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.setAvatarUrl("@user:matrix.org", "mxc://matrix.org/avatar")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test getPresence returns status`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "presence": "online",
                    "last_active_ago": 5000,
                    "currently_active": true
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getPresence("@user:matrix.org")

        assertTrue(result is MatrixResult.Success)
        val presence = (result as MatrixResult.Success).data
        assertEquals("online", presence.presence)
        assertEquals(5000L, presence.lastActiveAgo)
    }

    @Test
    fun `test setPresence success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.setPresence("@user:matrix.org", "online", "Working")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test uploadKeys success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "one_time_key_counts": {
                        "signed_curve25519": 50
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = KeysUploadRequest()
        val result = apiClient.uploadKeys(request)

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals(50, response.oneTimeKeyCounts["signed_curve25519"])
    }

    @Test
    fun `test queryKeys returns device keys`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "device_keys": {
                        "@user:matrix.org": {
                            "DEVICE_123": {
                                "user_id": "@user:matrix.org",
                                "device_id": "DEVICE_123",
                                "algorithms": ["m.olm.v1.curve25519-aes-sha2"],
                                "keys": {},
                                "signatures": {}
                            }
                        }
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val request = KeysQueryRequest(deviceKeys = mapOf("@user:matrix.org" to emptyList()))
        val result = apiClient.queryKeys(request)

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertTrue(response.deviceKeys.containsKey("@user:matrix.org"))
    }

    @Test
    fun `test getDevices returns device list`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "devices": [
                        {
                            "device_id": "DEVICE_123",
                            "display_name": "Android Phone"
                        }
                    ]
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getDevices()

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals(1, response.devices.size)
        assertEquals("DEVICE_123", response.devices[0].deviceId)
    }

    @Test
    fun `test getDevice returns device info`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "device_id": "DEVICE_123",
                    "display_name": "Android Phone",
                    "last_seen_ts": 1234567890
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.getDevice("DEVICE_123")

        assertTrue(result is MatrixResult.Success)
        val device = (result as MatrixResult.Success).data
        assertEquals("DEVICE_123", device.deviceId)
        assertEquals("Android Phone", device.displayName)
    }

    @Test
    fun `test updateDevice success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.updateDevice("DEVICE_123", "New Phone")

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test logout clears credentials`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val result = apiClient.logout()

        assertTrue(result is MatrixResult.Success)
    }

    @Test
    fun `test network error handling`() = runTest {
        mockWebServer.shutdown()

        val result = apiClient.login("user", "password")

        assertTrue(result is MatrixResult.NetworkError)
    }

    @Test
    fun `test rate limit error handling`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(429)
            .setBody("""
                {
                    "errcode": "M_LIMIT_EXCEEDED",
                    "error": "Too many requests",
                    "retry_after_ms": 2000
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val result = apiClient.login("user", "password")

        assertTrue(result is MatrixResult.Error)
        val error = result as MatrixResult.Error
        assertEquals("M_LIMIT_EXCEEDED", error.code)
        assertEquals(2000L, error.retryAfterMs)
    }

    @Test
    fun `test sendEncryptedMessage success`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "event_id": "${'$'}encrypted_event"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val encryptedMessage = SendEncryptedMessageRequest(
            senderKey = "key123",
            ciphertext = "encrypted_data",
            sessionId = "session123",
            deviceId = "DEVICE_123"
        )
        val result = apiClient.sendEncryptedMessage("!room1:matrix.org", encryptedMessage)

        assertTrue(result is MatrixResult.Success)
        val response = (result as MatrixResult.Success).data
        assertEquals("\$encrypted_event", response.eventId)
    }

    @Test
    fun `test clearCredentials removes authentication`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")
        apiClient.clearCredentials()

        // After clearing credentials, API calls should not have auth header
        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody("""{"errcode":"M_UNAUTHORIZED"}"""))

        val result = apiClient.getJoinedRooms()

        assertTrue(result is MatrixResult.Error)
    }

    @Test
    fun `test sync with filter parameter`() = runTest {
        apiClient.setCredentials("test_token", "@user:matrix.org", "DEVICE_123")

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"next_batch":"s1"}"""))

        val filter = SyncFilter(
            room = RoomFilter(
                timeline = RoomEventFilter(limit = 10)
            )
        )
        val result = apiClient.sync(filter = filter)

        assertTrue(result is MatrixResult.Success)
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("filter="))
    }
}
