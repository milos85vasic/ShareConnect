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


package com.shareconnect.jdownloaderconnect.data.database

import androidx.room.TypeConverter
import com.shareconnect.jdownloaderconnect.data.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JDownloaderConverters {

    @TypeConverter
    fun fromDownloadPriority(priority: DownloadPriority): String = priority.name

    @TypeConverter
    fun toDownloadPriority(priority: String): DownloadPriority = DownloadPriority.valueOf(priority)

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String = status.name

    @TypeConverter
    fun toDownloadStatus(status: String): DownloadStatus = DownloadStatus.valueOf(status)

    @TypeConverter
    fun fromLinkAvailability(availability: LinkAvailability): String = availability.name

    @TypeConverter
    fun toLinkAvailability(availability: String): LinkAvailability = LinkAvailability.valueOf(availability)

    @TypeConverter
    fun fromExtractorStatus(status: ExtractorStatus?): String? = status?.name

    @TypeConverter
    fun toExtractorStatus(status: String?): ExtractorStatus? = status?.let { ExtractorStatus.valueOf(it) }

    @TypeConverter
    fun fromStringList(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    fun toStringList(json: String): List<String> = Json.decodeFromString(json)
}