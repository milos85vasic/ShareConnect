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


package com.shareconnect.rsssync.models

import digital.vasic.asinka.models.SyncableObject

class SyncableRSSFeed(
    private var feedData: RSSFeedData
) : SyncableObject {

    override val objectId: String get() = feedData.id
    override val objectType: String get() = RSSFeedData.OBJECT_TYPE
    override val version: Int get() = feedData.version

    fun getFeedData(): RSSFeedData = feedData

    override fun toFieldMap(): Map<String, Any?> = mapOf(
        "id" to feedData.id,
        "url" to feedData.url,
        "name" to feedData.name,
        "autoDownload" to feedData.autoDownload,
        "filters" to feedData.filters,
        "excludeFilters" to feedData.excludeFilters,
        "updateInterval" to feedData.updateInterval,
        "lastUpdate" to feedData.lastUpdate,
        "isEnabled" to feedData.isEnabled,
        "category" to feedData.category,
        "torrentClientType" to feedData.torrentClientType,
        "downloadPath" to feedData.downloadPath,
        "sourceApp" to feedData.sourceApp,
        "version" to feedData.version,
        "lastModified" to feedData.lastModified
    )

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> feedData.version
        }

        val updateIntervalValue = when (val ui = fields["updateInterval"]) {
            is Int -> ui
            is Long -> ui.toInt()
            else -> feedData.updateInterval
        }

        val lastUpdateValue = when (val lu = fields["lastUpdate"]) {
            is Int -> lu.toLong()
            is Long -> lu
            else -> feedData.lastUpdate
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Int -> lm.toLong()
            is Long -> lm
            else -> feedData.lastModified
        }

        feedData = RSSFeedData(
            id = fields["id"] as? String ?: feedData.id,
            url = fields["url"] as? String ?: feedData.url,
            name = fields["name"] as? String ?: feedData.name,
            autoDownload = fields["autoDownload"] as? Boolean ?: feedData.autoDownload,
            filters = fields["filters"] as? String,
            excludeFilters = fields["excludeFilters"] as? String,
            updateInterval = updateIntervalValue,
            lastUpdate = lastUpdateValue,
            isEnabled = fields["isEnabled"] as? Boolean ?: feedData.isEnabled,
            category = fields["category"] as? String,
            torrentClientType = fields["torrentClientType"] as? String,
            downloadPath = fields["downloadPath"] as? String,
            sourceApp = fields["sourceApp"] as? String ?: feedData.sourceApp,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromFeedData(feedData: RSSFeedData) = SyncableRSSFeed(feedData)
    }
}
