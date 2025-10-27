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


package net.yupol.transmissionremote.mockserver.bencode

data class BencodeTorrent(
    val info: BencodeTorrentInfo,
    val comment: String,
    val creationDate: Long,
    val createdBy: String?
) {
    val totalSize: Long
        get() = info.length ?: info.files?.sumOf(BencodeFile::length) ?: 0

    companion object {
        fun fromMap(map: Map<String, Any?>): BencodeTorrent {
            return BencodeTorrent(
                info = BencodeTorrentInfo.fromMap(map["info"] as Map<String, Any?>),
                comment = map["comment"] as String,
                creationDate = map["creation date"] as Long,
                createdBy = map["created by"] as String?
            )
        }
    }
}

data class BencodeTorrentInfo(
    val name: String,
    val length: Long?,
    val files: List<BencodeFile>?
) {
    companion object {
        fun fromMap(infoMap: Map<String, Any?>): BencodeTorrentInfo {
            return BencodeTorrentInfo(
                name = infoMap["name"] as String,
                length = infoMap["length"] as Long?,
                files = (infoMap["files"] as? List<Map<String, Any>>)?.map(BencodeFile::fromMap)
            )
        }
    }
}

data class BencodeFile(
    val length: Long,
    val path: List<String>
) {
    companion object {
        fun fromMap(fileMap: Map<String, Any>): BencodeFile {
            return BencodeFile(
                length = fileMap["length"] as Long,
                path = fileMap["path"] as List<String>
            )
        }
    }
}
