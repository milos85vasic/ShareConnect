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


package com.shareconnect.jellyfinconnect.data.websocket

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.jellyfinconnect.TestApplication::class)
class JellyfinWebSocketMessagesTest {

    private val parser = JellyfinMessageParser()

    @Test
    fun `test KeepAliveMessage serialization without messageId`() {
        val message = KeepAliveMessage()

        assertEquals("KeepAlive", message.type)
        assertNull(message.messageId)

        val json = message.toJson()
        assertTrue(json.contains("\"MessageType\":\"KeepAlive\""))
        assertTrue(json.contains("\"Data\":{}"))
        assertFalse(json.contains("MessageId"))
    }

    @Test
    fun `test KeepAliveMessage serialization with messageId`() {
        val message = KeepAliveMessage(messageId = "test-123")

        assertEquals("KeepAlive", message.type)
        assertEquals("test-123", message.messageId)

        val json = message.toJson()
        assertTrue(json.contains("\"MessageType\":\"KeepAlive\""))
        assertTrue(json.contains("\"MessageId\":\"test-123\""))
        assertTrue(json.contains("\"Data\":{}"))
    }

    @Test
    fun `test SessionsStartMessage serialization`() {
        val message = SessionsStartMessage()

        assertEquals("SessionsStart", message.type)

        val json = message.toJson()
        assertTrue(json.contains("\"MessageType\":\"SessionsStart\""))
        assertTrue(json.contains("\"Data\":\"1000,1000\""))
    }

    @Test
    fun `test SessionsStopMessage serialization`() {
        val message = SessionsStopMessage(messageId = "stop-123")

        assertEquals("SessionsStop", message.type)
        assertEquals("stop-123", message.messageId)

        val json = message.toJson()
        assertTrue(json.contains("\"MessageType\":\"SessionsStop\""))
        assertTrue(json.contains("\"MessageId\":\"stop-123\""))
    }

    @Test
    fun `test parse ForceKeepAliveMessage`() {
        val json = """{"MessageType":"ForceKeepAlive","Data":60}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ForceKeepAliveMessage)
        assertEquals(60, (message as ForceKeepAliveMessage).data)
    }

    @Test
    fun `test parse KeepAliveMessage`() {
        val json = """{"MessageType":"KeepAlive","Data":{}}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is KeepAliveMessage)
        assertNull((message as KeepAliveMessage).messageId)
    }

    @Test
    fun `test parse SessionsMessage with minimal data`() {
        val json = """
            {
                "MessageType":"Sessions",
                "Data":[
                    {
                        "Id":"session-1",
                        "UserId":"user-1",
                        "UserName":"TestUser",
                        "Client":"Web",
                        "LastActivityDate":"2024-01-01T12:00:00Z",
                        "DeviceName":"Chrome",
                        "DeviceId":"device-1",
                        "ApplicationVersion":"10.8.0"
                    }
                ]
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is SessionsMessage)
        val sessionsMessage = message as SessionsMessage
        assertEquals(1, sessionsMessage.sessions.size)

        val session = sessionsMessage.sessions[0]
        assertEquals("session-1", session.id)
        assertEquals("user-1", session.userId)
        assertEquals("TestUser", session.userName)
        assertEquals("Web", session.client)
        assertEquals("Chrome", session.deviceName)
        assertNull(session.nowPlayingItem)
        assertNull(session.playState)
    }

    @Test
    fun `test parse SessionsMessage with now playing`() {
        val json = """
            {
                "MessageType":"Sessions",
                "Data":[
                    {
                        "Id":"session-1",
                        "UserId":"user-1",
                        "UserName":"TestUser",
                        "Client":"Android",
                        "LastActivityDate":"2024-01-01T12:00:00Z",
                        "DeviceName":"Pixel",
                        "DeviceId":"device-1",
                        "ApplicationVersion":"1.0.0",
                        "NowPlayingItem":{
                            "Id":"item-1",
                            "Name":"Test Movie",
                            "Type":"Movie",
                            "RunTimeTicks":72000000000,
                            "SeriesName":"Test Series",
                            "IndexNumber":5,
                            "ParentIndexNumber":2
                        },
                        "PlayState":{
                            "PositionTicks":36000000000,
                            "CanSeek":true,
                            "IsPaused":false,
                            "IsMuted":false,
                            "VolumeLevel":75,
                            "PlayMethod":"DirectPlay"
                        }
                    }
                ]
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is SessionsMessage)
        val sessionsMessage = message as SessionsMessage

        val session = sessionsMessage.sessions[0]
        assertNotNull(session.nowPlayingItem)
        assertNotNull(session.playState)

        val nowPlaying = session.nowPlayingItem!!
        assertEquals("item-1", nowPlaying.id)
        assertEquals("Test Movie", nowPlaying.name)
        assertEquals("Movie", nowPlaying.type)
        assertEquals(72000000000L, nowPlaying.runTimeTicks)
        assertEquals("Test Series", nowPlaying.seriesName)
        assertEquals(5, nowPlaying.indexNumber)
        assertEquals(2, nowPlaying.parentIndexNumber)

        val playState = session.playState!!
        assertEquals(36000000000L, playState.positionTicks)
        assertTrue(playState.canSeek)
        assertFalse(playState.isPaused)
        assertFalse(playState.isMuted)
        assertEquals(75, playState.volumeLevel)
        assertEquals("DirectPlay", playState.playMethod)
    }

    @Test
    fun `test parse UserDataChangedMessage`() {
        val json = """
            {
                "MessageType":"UserDataChanged",
                "Data":{
                    "UserDataList":[
                        {
                            "UserId":"user-1",
                            "UserItemDataList":[
                                {
                                    "ItemId":"item-1",
                                    "PlaybackPositionTicks":12000000000,
                                    "PlayCount":3,
                                    "IsFavorite":true,
                                    "Played":false,
                                    "LastPlayedDate":"2024-01-01T10:00:00Z"
                                },
                                {
                                    "ItemId":"item-2",
                                    "PlaybackPositionTicks":0,
                                    "PlayCount":1,
                                    "IsFavorite":false,
                                    "Played":true
                                }
                            ]
                        }
                    ]
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is UserDataChangedMessage)
        val userDataMessage = message as UserDataChangedMessage

        assertEquals(1, userDataMessage.userDataList.size)
        val userDataChange = userDataMessage.userDataList[0]
        assertEquals("user-1", userDataChange.userId)
        assertEquals(2, userDataChange.userDataList.size)

        val item1 = userDataChange.userDataList[0]
        assertEquals("item-1", item1.itemId)
        assertEquals(12000000000L, item1.playbackPositionTicks)
        assertEquals(3, item1.playCount)
        assertEquals(true, item1.isFavorite)
        assertEquals(false, item1.played)
        assertEquals("2024-01-01T10:00:00Z", item1.lastPlayedDate)

        val item2 = userDataChange.userDataList[1]
        assertEquals("item-2", item2.itemId)
        assertEquals(0L, item2.playbackPositionTicks)
        assertEquals(1, item2.playCount)
        assertEquals(false, item2.isFavorite)
        assertEquals(true, item2.played)
    }

    @Test
    fun `test parse LibraryChangedMessage`() {
        val json = """
            {
                "MessageType":"LibraryChanged",
                "Data":{
                    "ItemsAdded":["item-1","item-2"],
                    "ItemsUpdated":["item-3"],
                    "ItemsRemoved":["item-4","item-5","item-6"],
                    "FoldersAddedTo":["folder-1"],
                    "FoldersRemovedFrom":["folder-2"]
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is LibraryChangedMessage)
        val libraryMessage = message as LibraryChangedMessage

        assertEquals(2, libraryMessage.itemsAdded.size)
        assertEquals("item-1", libraryMessage.itemsAdded[0])
        assertEquals("item-2", libraryMessage.itemsAdded[1])

        assertEquals(1, libraryMessage.itemsUpdated.size)
        assertEquals("item-3", libraryMessage.itemsUpdated[0])

        assertEquals(3, libraryMessage.itemsRemoved.size)
        assertEquals("item-4", libraryMessage.itemsRemoved[0])

        assertEquals(1, libraryMessage.foldersAddedTo.size)
        assertEquals("folder-1", libraryMessage.foldersAddedTo[0])

        assertEquals(1, libraryMessage.foldersRemovedFrom.size)
        assertEquals("folder-2", libraryMessage.foldersRemovedFrom[0])
    }

    @Test
    fun `test parse PlayMessage`() {
        val json = """
            {
                "MessageType":"Play",
                "Data":{
                    "ItemIds":["item-1","item-2","item-3"],
                    "StartPositionTicks":5000000000,
                    "ControllingUserId":"user-1"
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is PlayMessage)
        val playMessage = message as PlayMessage

        assertEquals(3, playMessage.itemIds.size)
        assertEquals("item-1", playMessage.itemIds[0])
        assertEquals(5000000000L, playMessage.startPositionTicks)
        assertEquals("user-1", playMessage.controllingUserId)
    }

    @Test
    fun `test parse PlaystateMessage Stop`() {
        val json = """
            {
                "MessageType":"Playstate",
                "Data":{
                    "Command":"Stop"
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is PlaystateMessage)
        val playstateMessage = message as PlaystateMessage

        assertEquals(PlaystateMessage.PlaystateCommand.Stop, playstateMessage.command)
        assertNull(playstateMessage.seekPositionTicks)
    }

    @Test
    fun `test parse PlaystateMessage Seek with position`() {
        val json = """
            {
                "MessageType":"Playstate",
                "Data":{
                    "Command":"Seek",
                    "SeekPositionTicks":30000000000
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is PlaystateMessage)
        val playstateMessage = message as PlaystateMessage

        assertEquals(PlaystateMessage.PlaystateCommand.Seek, playstateMessage.command)
        assertEquals(30000000000L, playstateMessage.seekPositionTicks)
    }

    @Test
    fun `test parse GeneralCommandMessage`() {
        val json = """
            {
                "MessageType":"GeneralCommand",
                "Data":{
                    "Name":"DisplayMessage",
                    "Arguments":{
                        "Header":"Test Header",
                        "Text":"Test message content",
                        "TimeoutMs":"5000"
                    }
                }
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is GeneralCommandMessage)
        val generalCommand = message as GeneralCommandMessage

        assertEquals("DisplayMessage", generalCommand.name)
        assertEquals(3, generalCommand.arguments.size)
        assertEquals("Test Header", generalCommand.arguments["Header"])
        assertEquals("Test message content", generalCommand.arguments["Text"])
        assertEquals("5000", generalCommand.arguments["TimeoutMs"])
    }

    @Test
    fun `test parse ServerShuttingDownMessage`() {
        val json = """{"MessageType":"ServerShuttingDown","MessageId":"shutdown-1"}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ServerShuttingDownMessage)
        assertEquals("shutdown-1", (message as ServerShuttingDownMessage).messageId)
    }

    @Test
    fun `test parse ServerRestartingMessage`() {
        val json = """{"MessageType":"ServerRestarting"}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ServerRestartingMessage)
        assertNull((message as ServerRestartingMessage).messageId)
    }

    @Test
    fun `test parse ScheduledTasksInfoMessage`() {
        val json = """
            {
                "MessageType":"ScheduledTasksInfo",
                "Data":[
                    {
                        "Id":"task-1",
                        "Name":"Library Scan",
                        "State":"Running",
                        "CurrentProgressPercentage":45.5,
                        "LastExecutionResult":{
                            "StartTimeUtc":"2024-01-01T10:00:00Z",
                            "EndTimeUtc":"2024-01-01T10:15:00Z",
                            "Status":"Completed",
                            "Name":"Library Scan",
                            "Key":"LibraryScan"
                        }
                    },
                    {
                        "Id":"task-2",
                        "Name":"Backup",
                        "State":"Idle"
                    }
                ]
            }
        """.trimIndent()

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is ScheduledTasksInfoMessage)
        val tasksMessage = message as ScheduledTasksInfoMessage

        assertEquals(2, tasksMessage.tasks.size)

        val task1 = tasksMessage.tasks[0]
        assertEquals("task-1", task1.id)
        assertEquals("Library Scan", task1.name)
        assertEquals("Running", task1.state)
        assertEquals(45.5, task1.currentProgressPercentage!!, 0.01)

        assertNotNull(task1.lastExecutionResult)
        val result = task1.lastExecutionResult!!
        assertEquals("2024-01-01T10:00:00Z", result.startTimeUtc)
        assertEquals("2024-01-01T10:15:00Z", result.endTimeUtc)
        assertEquals("Completed", result.status)
        assertEquals("Library Scan", result.name)
        assertEquals("LibraryScan", result.key)

        val task2 = tasksMessage.tasks[1]
        assertEquals("task-2", task2.id)
        assertEquals("Backup", task2.name)
        assertEquals("Idle", task2.state)
        assertNull(task2.currentProgressPercentage)
        assertNull(task2.lastExecutionResult)
    }

    @Test
    fun `test parse empty Sessions array`() {
        val json = """{"MessageType":"Sessions","Data":[]}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is SessionsMessage)
        assertTrue((message as SessionsMessage).sessions.isEmpty())
    }

    @Test
    fun `test parse invalid JSON`() {
        val json = """{"invalid json"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse unknown message type`() {
        val json = """{"MessageType":"UnknownType","Data":{}}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse message without MessageType`() {
        val json = """{"Data":{"test":"value"}}"""

        val message = parser.parse(json)

        assertNull(message)
    }

    @Test
    fun `test parse LibraryChanged with empty data`() {
        val json = """{"MessageType":"LibraryChanged","Data":{}}"""

        val message = parser.parse(json)

        assertNotNull(message)
        assertTrue(message is LibraryChangedMessage)
        val libraryMessage = message as LibraryChangedMessage

        assertTrue(libraryMessage.itemsAdded.isEmpty())
        assertTrue(libraryMessage.itemsUpdated.isEmpty())
        assertTrue(libraryMessage.itemsRemoved.isEmpty())
    }
}
