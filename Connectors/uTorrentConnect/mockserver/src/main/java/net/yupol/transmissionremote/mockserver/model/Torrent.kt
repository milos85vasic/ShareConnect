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


package net.yupol.transmissionremote.mockserver.model

abstract class Torrent {
    data class Dates(
        val added: Long,
        val created: Long,
        val done: Long = 0,
        val activity: Long = added
    )
    data class Size(
        val totalSize: Long,
        val sizeWhenDone: Long = totalSize,
        val leftUntilDone: Long = sizeWhenDone,
        val desiredAvailable: Long = totalSize,
        val haveUnchecked: Long = 0,
        val haveValid: Long = 0,
        val downloadedEver: Long = 0,
        val uploadedEver: Long = 0,
        val pieceSize: Long = totalSize,
        val pieceCount: Long = 1
    )
}
