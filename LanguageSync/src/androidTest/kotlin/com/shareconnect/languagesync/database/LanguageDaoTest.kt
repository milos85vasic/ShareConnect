package com.shareconnect.languagesync.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguageDaoTest {

    private lateinit var database: LanguageDatabase
    private lateinit var languageDao: LanguageDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            LanguageDatabase::class.java
        ).allowMainThreadQueries().build()
        languageDao = database.languageDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndGetLanguagePreference() = runBlocking {
        val language = LanguageData.createDefault()

        languageDao.insertLanguagePreference(language)
        val result = languageDao.getLanguagePreference()

        assertNotNull(result)
        assertEquals(language.id, result?.id)
        assertEquals(language.languageCode, result?.languageCode)
        assertEquals(language.displayName, result?.displayName)
        assertEquals(language.isSystemDefault, result?.isSystemDefault)
    }

    @Test
    fun testGetLanguagePreferenceReturnsNullWhenEmpty() = runBlocking {
        val result = languageDao.getLanguagePreference()
        assertNull(result)
    }

    @Test
    fun testInsertReplacesExisting() = runBlocking {
        val language1 = LanguageData.createDefault()
        languageDao.insertLanguagePreference(language1)

        val language2 = language1.copy(
            languageCode = LanguageData.CODE_ENGLISH,
            displayName = LanguageData.NAME_ENGLISH,
            version = 2
        )
        languageDao.insertLanguagePreference(language2)

        val result = languageDao.getLanguagePreference()

        assertNotNull(result)
        assertEquals(LanguageData.CODE_ENGLISH, result?.languageCode)
        assertEquals(2, result?.version)
    }

    @Test
    fun testUpdateLanguagePreference() = runBlocking {
        val language = LanguageData.createDefault()
        languageDao.insertLanguagePreference(language)

        val updated = language.copy(
            languageCode = LanguageData.CODE_SPANISH,
            displayName = LanguageData.NAME_SPANISH,
            version = 3
        )
        languageDao.updateLanguagePreference(updated)

        val result = languageDao.getLanguagePreference()

        assertNotNull(result)
        assertEquals(LanguageData.CODE_SPANISH, result?.languageCode)
        assertEquals(LanguageData.NAME_SPANISH, result?.displayName)
        assertEquals(3, result?.version)
    }

    @Test
    fun testGetLanguagePreferenceFlow() = runBlocking {
        val language = LanguageData.createDefault()
        languageDao.insertLanguagePreference(language)

        val result = languageDao.getLanguagePreferenceFlow().first()

        assertNotNull(result)
        assertEquals(language.id, result?.id)
        assertEquals(language.languageCode, result?.languageCode)
    }

    @Test
    fun testFlowEmitsUpdates() = runBlocking {
        // Insert initial value
        val initial = LanguageData.createDefault()
        languageDao.insertLanguagePreference(initial)

        // Get first emission
        val first = languageDao.getLanguagePreferenceFlow().first()
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, first?.languageCode)

        // Update value
        val updated = initial.copy(
            languageCode = LanguageData.CODE_FRENCH,
            displayName = LanguageData.NAME_FRENCH
        )
        languageDao.insertLanguagePreference(updated)

        // Get updated emission
        val second = languageDao.getLanguagePreferenceFlow().first()
        assertEquals(LanguageData.CODE_FRENCH, second?.languageCode)
    }

    @Test
    fun testAllLanguageCodes() = runBlocking {
        val languages = listOf(
            LanguageData.CODE_ENGLISH to LanguageData.NAME_ENGLISH,
            LanguageData.CODE_SPANISH to LanguageData.NAME_SPANISH,
            LanguageData.CODE_FRENCH to LanguageData.NAME_FRENCH,
            LanguageData.CODE_GERMAN to LanguageData.NAME_GERMAN,
            LanguageData.CODE_RUSSIAN to LanguageData.NAME_RUSSIAN,
            LanguageData.CODE_CHINESE to LanguageData.NAME_CHINESE,
            LanguageData.CODE_JAPANESE to LanguageData.NAME_JAPANESE,
            LanguageData.CODE_KOREAN to LanguageData.NAME_KOREAN
        )

        // Test inserting and retrieving each language
        for ((code, name) in languages) {
            val language = LanguageData(
                id = "language_preference",
                languageCode = code,
                displayName = name,
                isSystemDefault = false,
                version = 1
            )
            languageDao.insertLanguagePreference(language)

            val result = languageDao.getLanguagePreference()
            assertNotNull(result)
            assertEquals(code, result?.languageCode)
            assertEquals(name, result?.displayName)
        }
    }

    @Test
    fun testVersionIncrementsCorrectly() = runBlocking {
        var language = LanguageData.createDefault()

        // Insert versions 1-5
        for (v in 1..5) {
            language = language.copy(version = v)
            languageDao.insertLanguagePreference(language)

            val result = languageDao.getLanguagePreference()
            assertEquals(v, result?.version)
        }
    }

    @Test
    fun testLastModifiedTimestampPreserved() = runBlocking {
        val timestamp = 123456789L
        val language = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_ITALIAN,
            displayName = LanguageData.NAME_ITALIAN,
            isSystemDefault = false,
            version = 1,
            lastModified = timestamp
        )

        languageDao.insertLanguagePreference(language)
        val result = languageDao.getLanguagePreference()

        assertEquals(timestamp, result?.lastModified)
    }
}
