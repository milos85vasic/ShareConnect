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


package com.shareconnect.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "history_items")
@TypeConverters(Converters::class)
class HistoryItem {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var url: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnailUrl: String? = null
    var serviceProvider: String? = null
    var type: String? = null // single_video, playlist, channel
    var timestamp: Long = 0
    var profileId: String? = null
    var profileName: String? = null
    var isSentSuccessfully: Boolean = false
    var serviceType: String? = null // MeTube, Torrent, jDownloader

    constructor()

    @androidx.room.Ignore
    constructor(
        url: String?,
        title: String?,
        description: String?,
        thumbnailUrl: String?,
        serviceProvider: String?,
        type: String?,
        timestamp: Long,
        profileId: String?,
        profileName: String?,
        isSentSuccessfully: Boolean,
        serviceType: String?
    ) {
        this.url = url
        this.title = title
        this.description = description
        this.thumbnailUrl = thumbnailUrl
        this.serviceProvider = serviceProvider
        this.type = type
        this.timestamp = timestamp
        this.profileId = profileId
        this.profileName = profileName
        this.isSentSuccessfully = isSentSuccessfully
        this.serviceType = serviceType
    }
    
    @androidx.room.Ignore
    constructor(
        url: String?,
        title: String?,
        serviceProvider: String?,
        type: String?,
        timestamp: Long,
        profileId: String?,
        profileName: String?,
        isSentSuccessfully: Boolean,
        serviceType: String?
    ) {
        this.url = url
        this.title = title
        this.serviceProvider = serviceProvider
        this.type = type
        this.timestamp = timestamp
        this.profileId = profileId
        this.profileName = profileName
        this.isSentSuccessfully = isSentSuccessfully
        this.serviceType = serviceType
    }
}