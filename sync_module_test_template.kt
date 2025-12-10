package com.shareconnect.sync.[modulename].test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.sync.[modulename].manager.[ModuleName]SyncManager
import com.shareconnect.sync.[modulename].models.[EntityName]Data
import com.shareconnect.sync.[modulename].repository.[ModuleName]Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class [ModuleName]SyncManagerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: [ModuleName]Repository

    private lateinit var syncManager: [ModuleName]SyncManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        syncManager = [ModuleName]SyncManager.getInstance(
            context = ApplicationProvider.getApplicationContext(),
            appIdentifier = "com.shareconnect.test",
            appName = "ShareConnect Test",
            appVersion = "1.0.0-test"
        )
    }

    @After
    fun tearDown() {
        // Clean up resources if needed
    }

    @Test
    fun `test singleton instance creation`() {
        val instance1 = [ModuleName]SyncManager.getInstance(
            context = ApplicationProvider.getApplicationContext(),
            appIdentifier = "com.shareconnect.test",
            appName = "ShareConnect Test",
            appVersion = "1.0.0-test"
        )
        val instance2 = [ModuleName]SyncManager.getInstance(
            context = ApplicationProvider.getApplicationContext(),
            appIdentifier = "com.shareconnect.test",
            appName = "ShareConnect Test",
            appVersion = "1.0.0-test"
        )

        assertEquals(instance1, instance2, "Singleton instances should be the same")
    }

    @Test
    fun `create and save entity successfully`() = runTest {
        val entity = [EntityName]Data(
            id = "test_id_001",
            name = "Test Entity"
        )

        val result = syncManager.save[EntityName](entity)
        assertTrue(result, "Entity should be saved successfully")
    }

    @Test
    fun `retrieve entity by id`() = runTest {
        val entity = [EntityName]Data(
            id = "test_id_002",
            name = "Retrievable Entity"
        )
        syncManager.save[EntityName](entity)

        val retrievedEntity = syncManager.get[EntityName]ById("test_id_002")
        assertNotNull(retrievedEntity, "Retrieved entity should not be null")
        assertEquals(entity.id, retrievedEntity?.id, "Retrieved entity should match saved entity")
    }

    @Test
    fun `delete entity successfully`() = runTest {
        val entity = [EntityName]Data(
            id = "test_id_003",
            name = "Deletable Entity"
        )
        syncManager.save[EntityName](entity)

        val deleteResult = syncManager.delete[EntityName]("test_id_003")
        assertTrue(deleteResult, "Entity should be deleted successfully")

        val deletedEntity = syncManager.get[EntityName]ById("test_id_003")
        assertTrue(deletedEntity == null, "Deleted entity should not exist")
    }

    @Test
    fun `list all entities`() = runTest {
        // Prepare test data
        val entities = listOf(
            [EntityName]Data(id = "test_id_004", name = "Entity 1"),
            [EntityName]Data(id = "test_id_005", name = "Entity 2")
        )

        entities.forEach { syncManager.save[EntityName](it) }

        val allEntities = syncManager.getAll[EntityName]s()
        assertTrue(allEntities.size >= 2, "Should retrieve at least two entities")
    }

    @Test(expected = [ModuleName]ValidationException::class)
    fun `save invalid entity should throw exception`() = runTest {
        val invalidEntity = [EntityName]Data(
            id = "", // Empty ID should cause validation failure
            name = ""
        )
        syncManager.save[EntityName](invalidEntity)
    }

    // Performance and stress tests
    @Test
    fun `synchronization performance`() = runTest {
        val startTime = System.currentTimeMillis()

        // Create and save multiple entities
        repeat(100) { index ->
            val entity = [EntityName]Data(
                id = "perf_test_$index",
                name = "Performance Test Entity $index"
            )
            syncManager.save[EntityName](entity)
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        assertTrue(duration < 1000, "Synchronization of 100 entities should complete within 1 second")
    }
}