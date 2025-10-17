package com.shareconnect.rsssync.models

import org.junit.Assert.*
import org.junit.Test

class RSSFeedDataTest {

    @Test
    fun `test RSSFeedData creation with all fields`() {
        // Given
        val id = "test-feed-id"
        val url = "https://example.com/feed.xml"
        val title = "Test Feed"
        val description = "Test RSS Feed Description"
        val language = "en"
        val copyright = "Copyright 2024"
        val managingEditor = "editor@example.com"
        val webMaster = "webmaster@example.com"
        val pubDate = System.currentTimeMillis()
        val lastBuildDate = System.currentTimeMillis()
        val category = "Technology"
        val generator = "RSS Generator"
        val docs = "https://www.rssboard.org/rss-specification"
        val cloudDomain = "rpc.example.com"
        val cloudPort = 80
        val cloudPath = "/RPC2"
        val cloudProcedure = "example.getStateName"
        val cloudProtocol = "xml-rpc"
        val ttl = 60
        val imageUrl = "https://example.com/image.jpg"
        val imageTitle = "Feed Image"
        val imageLink = "https://example.com"
        val imageWidth = 144
        val imageHeight = 400
        val imageDescription = "Feed image description"
        val textInputTitle = "Text Input"
        val textInputDescription = "Enter your query"
        val textInputName = "query"
        val textInputLink = "https://example.com/search"
        val skipHours = "0,6,12,18"
        val skipDays = "Saturday,Sunday"
        val timestamp = System.currentTimeMillis()
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val feedData = RSSFeedData(
            id = id,
            url = url,
            title = title,
            description = description,
            language = language,
            copyright = copyright,
            managingEditor = managingEditor,
            webMaster = webMaster,
            pubDate = pubDate,
            lastBuildDate = lastBuildDate,
            category = category,
            generator = generator,
            docs = docs,
            cloudDomain = cloudDomain,
            cloudPort = cloudPort,
            cloudPath = cloudPath,
            cloudProcedure = cloudProcedure,
            cloudProtocol = cloudProtocol,
            ttl = ttl,
            imageUrl = imageUrl,
            imageTitle = imageTitle,
            imageLink = imageLink,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            imageDescription = imageDescription,
            textInputTitle = textInputTitle,
            textInputDescription = textInputDescription,
            textInputName = textInputName,
            textInputLink = textInputLink,
            skipHours = skipHours,
            skipDays = skipDays,
            timestamp = timestamp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, feedData.id)
        assertEquals(url, feedData.url)
        assertEquals(title, feedData.title)
        assertEquals(description, feedData.description)
        assertEquals(language, feedData.language)
        assertEquals(copyright, feedData.copyright)
        assertEquals(managingEditor, feedData.managingEditor)
        assertEquals(webMaster, feedData.webMaster)
        assertEquals(pubDate, feedData.pubDate)
        assertEquals(lastBuildDate, feedData.lastBuildDate)
        assertEquals(category, feedData.category)
        assertEquals(generator, feedData.generator)
        assertEquals(docs, feedData.docs)
        assertEquals(cloudDomain, feedData.cloudDomain)
        assertEquals(cloudPort, feedData.cloudPort)
        assertEquals(cloudPath, feedData.cloudPath)
        assertEquals(cloudProcedure, feedData.cloudProcedure)
        assertEquals(cloudProtocol, feedData.cloudProtocol)
        assertEquals(ttl, feedData.ttl)
        assertEquals(imageUrl, feedData.imageUrl)
        assertEquals(imageTitle, feedData.imageTitle)
        assertEquals(imageLink, feedData.imageLink)
        assertEquals(imageWidth, feedData.imageWidth)
        assertEquals(imageHeight, feedData.imageHeight)
        assertEquals(imageDescription, feedData.imageDescription)
        assertEquals(textInputTitle, feedData.textInputTitle)
        assertEquals(textInputDescription, feedData.textInputDescription)
        assertEquals(textInputName, feedData.textInputName)
        assertEquals(textInputLink, feedData.textInputLink)
        assertEquals(skipHours, feedData.skipHours)
        assertEquals(skipDays, feedData.skipDays)
        assertEquals(timestamp, feedData.timestamp)
        assertEquals(version, feedData.version)
        assertEquals(lastModified, feedData.lastModified)
    }

    @Test
    fun `test RSSFeedData creation with minimal fields`() {
        // Given
        val id = "test-feed-id"
        val url = "https://example.com/feed.xml"
        val title = "Test Feed"
        val timestamp = System.currentTimeMillis()

        // When
        val feedData = RSSFeedData(
            id = id,
            url = url,
            title = title,
            timestamp = timestamp
        )

        // Then
        assertEquals(id, feedData.id)
        assertEquals(url, feedData.url)
        assertEquals(title, feedData.title)
        assertEquals(timestamp, feedData.timestamp)
        assertNull(feedData.description)
        assertNull(feedData.language)
        assertNull(feedData.copyright)
        assertNull(feedData.managingEditor)
        assertNull(feedData.webMaster)
        assertEquals(0L, feedData.pubDate)
        assertEquals(0L, feedData.lastBuildDate)
        assertNull(feedData.category)
        assertNull(feedData.generator)
        assertNull(feedData.docs)
        assertNull(feedData.cloudDomain)
        assertEquals(0, feedData.cloudPort)
        assertNull(feedData.cloudPath)
        assertNull(feedData.cloudProcedure)
        assertNull(feedData.cloudProtocol)
        assertEquals(0, feedData.ttl)
        assertNull(feedData.imageUrl)
        assertNull(feedData.imageTitle)
        assertNull(feedData.imageLink)
        assertEquals(0, feedData.imageWidth)
        assertEquals(0, feedData.imageHeight)
        assertNull(feedData.imageDescription)
        assertNull(feedData.textInputTitle)
        assertNull(feedData.textInputDescription)
        assertNull(feedData.textInputName)
        assertNull(feedData.textInputLink)
        assertNull(feedData.skipHours)
        assertNull(feedData.skipDays)
        assertEquals(0, feedData.version)
        assertEquals(0L, feedData.lastModified)
    }

    @Test
    fun `test RSSFeedData equals and hashCode work correctly`() {
        // Given
        val feed1 = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis()
        )
        val feed2 = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis()
        )
        val feed3 = RSSFeedData(
            id = "different-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis()
        )

        // Then
        assertEquals(feed1, feed2)
        assertEquals(feed1.hashCode(), feed2.hashCode())
        assertNotEquals(feed1, feed3)
        assertNotEquals(feed1.hashCode(), feed3.hashCode())
    }

    @Test
    fun `test RSSFeedData toString contains relevant information`() {
        // Given
        val feedData = RSSFeedData(
            id = "test-feed-id",
            url = "https://example.com/feed.xml",
            title = "Test Feed",
            timestamp = System.currentTimeMillis()
        )

        // When
        val toString = feedData.toString()

        // Then
        assertTrue(toString.contains("test-feed-id"))
        assertTrue(toString.contains("https://example.com/feed.xml"))
        assertTrue(toString.contains("Test Feed"))
    }
}