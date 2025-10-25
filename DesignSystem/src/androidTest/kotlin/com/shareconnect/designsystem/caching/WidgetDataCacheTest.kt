package com.shareconnect.designsystem.caching

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for WidgetDataCache
 */
@RunWith(AndroidJUnit4::class)
class WidgetDataCacheTest {

    private lateinit var cache: WidgetDataCache
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() = runBlocking {
        cache = WidgetDataCache(ApplicationProvider.getApplicationContext())
        cache.clearAllCaches()
    }

    @After
    fun tearDown() = runBlocking {
        cache.clearAllCaches()
    }

    @Test
    fun saveAndLoadWidgetData() = runBlocking {
        val testData = TestData("test", 42)

        cache.saveWidgetData(
            widgetKey = "test_widget",
            data = testData,
            serializer = { json.encodeToString(it) }
        )

        val loaded = cache.loadWidgetData<TestData>(
            widgetKey = "test_widget",
            deserializer = { json.decodeFromString(it) }
        )

        assertNotNull(loaded)
        assertEquals(testData.name, loaded!!.data.name)
        assertEquals(testData.value, loaded.data.value)
        assertFalse(loaded.isStale)
    }

    @Test
    fun loadNonExistentWidget_returnsNull() = runBlocking {
        val loaded = cache.loadWidgetData<TestData>(
            widgetKey = "non_existent",
            deserializer = { json.decodeFromString(it) }
        )

        assertNull(loaded)
    }

    @Test
    fun cacheBecomesStaleAfterTimeout() = runBlocking {
        val testData = TestData("test", 42)

        cache.saveWidgetData(
            widgetKey = "test_widget",
            data = testData,
            serializer = { json.encodeToString(it) }
        )

        // Immediately after save, should be fresh
        assertTrue(cache.isCacheFresh("test_widget"))

        val loaded = cache.loadWidgetData<TestData>(
            widgetKey = "test_widget",
            deserializer = { json.decodeFromString(it) }
        )

        assertNotNull(loaded)
        assertFalse(loaded!!.isStale) // Should be fresh
    }

    @Test
    fun clearWidgetCache_removesData() = runBlocking {
        val testData = TestData("test", 42)

        cache.saveWidgetData(
            widgetKey = "test_widget",
            data = testData,
            serializer = { json.encodeToString(it) }
        )

        // Verify it was saved
        var loaded = cache.loadWidgetData<TestData>(
            widgetKey = "test_widget",
            deserializer = { json.decodeFromString(it) }
        )
        assertNotNull(loaded)

        // Clear cache
        cache.clearWidgetCache("test_widget")

        // Verify it was removed
        loaded = cache.loadWidgetData<TestData>(
            widgetKey = "test_widget",
            deserializer = { json.decodeFromString(it) }
        )
        assertNull(loaded)
    }

    @Test
    fun clearAllCaches_removesAllData() = runBlocking {
        val testData1 = TestData("test1", 1)
        val testData2 = TestData("test2", 2)

        cache.saveWidgetData("widget1", testData1) { json.encodeToString(it) }
        cache.saveWidgetData("widget2", testData2) { json.encodeToString(it) }

        // Clear all
        cache.clearAllCaches()

        // Verify both removed
        assertNull(cache.loadWidgetData<TestData>("widget1") { json.decodeFromString(it) })
        assertNull(cache.loadWidgetData<TestData>("widget2") { json.decodeFromString(it) })
    }

    @Test
    fun observeWidgetData_receivesUpdates() = runBlocking {
        val testData1 = TestData("test1", 1)
        val testData2 = TestData("test2", 2)

        // Save initial data
        cache.saveWidgetData("observed_widget", testData1) { json.encodeToString(it) }

        // Observe
        val flow = cache.observeWidgetData<TestData>("observed_widget") {
            json.decodeFromString(it)
        }

        // Get initial value
        val initial = flow.first()
        assertNotNull(initial)
        assertEquals("test1", initial!!.data.name)

        // Update data
        cache.saveWidgetData("observed_widget", testData2) { json.encodeToString(it) }

        // Get updated value
        val updated = flow.first()
        assertNotNull(updated)
        assertEquals("test2", updated!!.data.name)
    }

    @Test
    fun isCacheFresh_checksExpiration() = runBlocking {
        val testData = TestData("test", 42)

        // No cache yet
        assertFalse(cache.isCacheFresh("test_widget"))

        // Save cache
        cache.saveWidgetData("test_widget", testData) { json.encodeToString(it) }

        // Should be fresh immediately
        assertTrue(cache.isCacheFresh("test_widget"))
    }

    @Test
    fun cachedDataIncludesTimestamp() = runBlocking {
        val testData = TestData("test", 42)
        val beforeSave = System.currentTimeMillis()

        cache.saveWidgetData("test_widget", testData) { json.encodeToString(it) }

        val afterSave = System.currentTimeMillis()

        val loaded = cache.loadWidgetData<TestData>("test_widget") {
            json.decodeFromString(it)
        }

        assertNotNull(loaded)
        assertTrue(loaded!!.timestamp >= beforeSave)
        assertTrue(loaded.timestamp <= afterSave)
        assertTrue(loaded.ageMinutes >= 0)
    }

    @Test
    fun saveHomeAssistantData_worksCorrectly() = runBlocking {
        cache.saveHomeAssistantData(
            lightsOn = 5,
            totalLights = 10,
            switchesOn = 3,
            totalSwitches = 8,
            totalSensors = 15,
            isConnected = true
        )

        val loaded = cache.loadHomeAssistantData()

        assertNotNull(loaded)
        assertEquals(5, loaded!!.data.lightsOn)
        assertEquals(10, loaded.data.totalLights)
        assertEquals(3, loaded.data.switchesOn)
        assertEquals(8, loaded.data.totalSwitches)
        assertEquals(15, loaded.data.totalSensors)
        assertTrue(loaded.data.isConnected)
    }

    @Test
    fun saveJellyfinData_worksCorrectly() = runBlocking {
        cache.saveJellyfinData(
            nowPlaying = "Movie Title",
            activeSessions = 2,
            playing = 1,
            isConnected = true
        )

        val loaded = cache.loadJellyfinData()

        assertNotNull(loaded)
        assertEquals("Movie Title", loaded!!.data.nowPlaying)
        assertEquals(2, loaded.data.activeSessions)
        assertEquals(1, loaded.data.playing)
        assertTrue(loaded.data.isConnected)
    }

    @Test
    fun saveNetdataData_worksCorrectly() = runBlocking {
        cache.saveNetdataData(
            healthStatus = "OK",
            cpuUsage = 45,
            ramUsage = 60,
            diskUsage = 70,
            criticalAlarms = 0,
            warningAlarms = 2
        )

        val loaded = cache.loadNetdataData()

        assertNotNull(loaded)
        assertEquals("OK", loaded!!.data.healthStatus)
        assertEquals(45, loaded.data.cpuUsage)
        assertEquals(60, loaded.data.ramUsage)
        assertEquals(70, loaded.data.diskUsage)
        assertEquals(0, loaded.data.criticalAlarms)
        assertEquals(2, loaded.data.warningAlarms)
    }

    @Test
    fun savePortainerData_worksCorrectly() = runBlocking {
        cache.savePortainerData(
            runningContainers = 5,
            stoppedContainers = 2,
            imagesCount = 10,
            totalContainers = 7,
            isConnected = true
        )

        val loaded = cache.loadPortainerData()

        assertNotNull(loaded)
        assertEquals(5, loaded!!.data.runningContainers)
        assertEquals(2, loaded.data.stoppedContainers)
        assertEquals(10, loaded.data.imagesCount)
        assertEquals(7, loaded.data.totalContainers)
        assertTrue(loaded.data.isConnected)
    }

    @Test
    fun handleInvalidJsonGracefully() = runBlocking {
        // Manually corrupt the cache with invalid JSON
        cache.saveWidgetData(
            widgetKey = "corrupt_widget",
            data = "invalid json {{{",
            serializer = { it }
        )

        val loaded = cache.loadWidgetData<TestData>(
            widgetKey = "corrupt_widget",
            deserializer = { json.decodeFromString(it) }
        )

        // Should return null instead of crashing
        assertNull(loaded)
    }

    @Serializable
    data class TestData(
        val name: String,
        val value: Int
    )
}
