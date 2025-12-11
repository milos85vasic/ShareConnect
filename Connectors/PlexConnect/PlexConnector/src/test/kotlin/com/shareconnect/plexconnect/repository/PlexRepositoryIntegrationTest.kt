package com.shareconnect.plexconnect.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.repository.PlexRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PlexRepositoryIntegrationTest {

    private lateinit var database: com.shareconnect.plexconnect.data.database.PlexDatabase
    private lateinit var repository: PlexRepositoryImpl

    @Before
    fun setup() {
        // Create an in-memory database
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = androidx.room.Room.inMemoryDatabaseBuilder(
            context, com.shareconnect.plexconnect.data.database.PlexDatabase::class.java
        ).build()

        // Create repository (currently stubbed)
        repository = PlexRepositoryImpl()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun `repository can be instantiated`() = runBlocking {
        // Basic test that repository can be created
        assertNotNull(repository)
    }
}