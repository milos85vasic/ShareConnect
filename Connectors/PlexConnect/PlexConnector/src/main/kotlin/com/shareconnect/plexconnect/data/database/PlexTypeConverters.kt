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


package com.shareconnect.plexconnect.data.database

import androidx.room.TypeConverter
import com.shareconnect.plexconnect.data.model.LibraryType
import com.shareconnect.plexconnect.data.model.MediaType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlexTypeConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // LibraryType converters
    @TypeConverter
    fun fromLibraryType(type: LibraryType): String = type.value

    @TypeConverter
    fun toLibraryType(value: String): LibraryType = LibraryType.fromString(value)

    // MediaType converters
    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.value

    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.fromString(value)

    // List<String> converters
    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)

    // FloatArray converters for embeddings
    @TypeConverter
    fun fromFloatArray(array: FloatArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(array.size * 4)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        byteBuffer.asFloatBuffer().put(array)
        return byteBuffer.array()
    }

    @TypeConverter
    fun toFloatArray(bytes: ByteArray): FloatArray {
        val byteBuffer = ByteBuffer.wrap(bytes)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        val floatArray = FloatArray(bytes.size / 4)
        byteBuffer.asFloatBuffer().get(floatArray)
        return floatArray
    }
}