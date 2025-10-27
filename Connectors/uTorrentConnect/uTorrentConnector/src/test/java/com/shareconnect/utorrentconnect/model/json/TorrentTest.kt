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


package com.shareconnect.utorrentconnect.model.json

import android.os.Parcel
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for Torrent model.
 * Tests Builder pattern, getters, Parcelable implementation, and hash support.
 */
@RunWith(RobolectricTestRunner::class)
class TorrentTest {

    @Test
    fun builder_allFields_setsCorrectly() {
        val torrent = Torrent.Builder()
            .id(12345)
            .hash("abcdef1234567890")
            .name("Test Torrent")
            .addedDate(1609459200)
            .totalSize(1073741824)
            .percentDone(0.75)
            .status(Torrent.Status.DOWNLOAD.value)
            .downloadRate(102400)
            .uploadRate(51200)
            .eta(3600)
            .uploadedSize(204800)
            .uploadRatio(2.0)
            .errorId(0)
            .errorString("")
            .isFinished(false)
            .sizeWhenDone(1073741824)
            .leftUntilDone(268435456)
            .peersGettingFromUs(5)
            .peersSendingToUs(10)
            .webseedsSendingToUs(2)
            .queuePosition(1)
            .recheckProgress(0.0)
            .doneDate(0)
            .activityDate(1609459200)
            .build()

        assertThat(torrent.id).isEqualTo(12345)
        assertThat(torrent.hash).isEqualTo("abcdef1234567890")
        assertThat(torrent.name).isEqualTo("Test Torrent")
        assertThat(torrent.addedDate).isEqualTo(1609459200)
        assertThat(torrent.totalSize).isEqualTo(1073741824)
        assertThat(torrent.percentDone).isWithin(0.001).of(0.75)
        assertThat(torrent.status).isEqualTo(Torrent.Status.DOWNLOAD)
        assertThat(torrent.downloadRate).isEqualTo(102400)
        assertThat(torrent.uploadRate).isEqualTo(51200)
        assertThat(torrent.eta).isEqualTo(3600)
        assertThat(torrent.uploadedSize).isEqualTo(204800)
        assertThat(torrent.uploadRatio).isWithin(0.001).of(2.0)
        assertThat(torrent.errorId).isEqualTo(0)
        assertThat(torrent.errorMessage).isEmpty()
        assertThat(torrent.isFinished).isFalse()
        assertThat(torrent.sizeWhenDone).isEqualTo(1073741824)
        assertThat(torrent.leftUntilDone).isEqualTo(268435456)
        assertThat(torrent.peersGettingFromUs).isEqualTo(5)
        assertThat(torrent.peersSendingToUs).isEqualTo(10)
        assertThat(torrent.webseedsSendingToUs).isEqualTo(2)
        assertThat(torrent.queuePosition).isEqualTo(1)
        assertThat(torrent.recheckProgress).isWithin(0.001).of(0.0)
        assertThat(torrent.doneDate).isEqualTo(0)
        assertThat(torrent.activityDate).isEqualTo(1609459200)
    }

    @Test
    fun builder_chainedCalls_returnsBuilderInstance() {
        val builder = Torrent.Builder()
            .id(1)
            .hash("hash")
            .name("name")

        assertThat(builder).isInstanceOf(Torrent.Builder::class.java)
    }

    @Test
    fun hashGetterSetter_setsAndGetsCorrectly() {
        val torrent = Torrent()
        torrent.hash = "test_hash_value"

        assertThat(torrent.hash).isEqualTo("test_hash_value")
    }

    @Test
    fun getHash_nullHash_returnsNull() {
        val torrent = Torrent.Builder().build()

        assertThat(torrent.hash).isNull()
    }

    @Test
    fun getPeersGettingFromUs_returnsCorrectValue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(15)
            .build()

        assertThat(torrent.peersGettingFromUs).isEqualTo(15)
    }

    @Test
    fun getPeersSendingToUs_returnsCorrectValue() {
        val torrent = Torrent.Builder()
            .peersSendingToUs(25)
            .build()

        assertThat(torrent.peersSendingToUs).isEqualTo(25)
    }

    @Test
    fun getWebseedsSendingToUs_returnsCorrectValue() {
        val torrent = Torrent.Builder()
            .webseedsSendingToUs(3)
            .build()

        assertThat(torrent.webseedsSendingToUs).isEqualTo(3)
    }

    @Test
    fun status_fromValue_returnsCorrectStatus() {
        assertThat(Torrent.Status.fromValue(0)).isEqualTo(Torrent.Status.STOPPED)
        assertThat(Torrent.Status.fromValue(1)).isEqualTo(Torrent.Status.CHECK_WAIT)
        assertThat(Torrent.Status.fromValue(2)).isEqualTo(Torrent.Status.CHECK)
        assertThat(Torrent.Status.fromValue(3)).isEqualTo(Torrent.Status.DOWNLOAD_WAIT)
        assertThat(Torrent.Status.fromValue(4)).isEqualTo(Torrent.Status.DOWNLOAD)
        assertThat(Torrent.Status.fromValue(5)).isEqualTo(Torrent.Status.SEED_WAIT)
        assertThat(Torrent.Status.fromValue(6)).isEqualTo(Torrent.Status.SEED)
    }

    @Test
    fun status_fromValue_unknownValue_returnsUnknown() {
        assertThat(Torrent.Status.fromValue(99)).isEqualTo(Torrent.Status.UNKNOWN)
        assertThat(Torrent.Status.fromValue(-1)).isEqualTo(Torrent.Status.UNKNOWN)
    }

    @Test
    fun error_getById_returnsCorrectError() {
        assertThat(Torrent.Error.getById(0)).isEqualTo(Torrent.Error.NONE)
        assertThat(Torrent.Error.getById(1)).isEqualTo(Torrent.Error.TRACKER_WARNING)
        assertThat(Torrent.Error.getById(2)).isEqualTo(Torrent.Error.TRACKER_ERROR)
        assertThat(Torrent.Error.getById(3)).isEqualTo(Torrent.Error.LOCAL_ERROR)
    }

    @Test
    fun error_getById_unknownId_returnsUnknown() {
        assertThat(Torrent.Error.getById(99)).isEqualTo(Torrent.Error.UNKNOWN)
    }

    @Test
    fun error_isWarning_trackerWarning_returnsTrue() {
        assertThat(Torrent.Error.TRACKER_WARNING.isWarning()).isTrue()
    }

    @Test
    fun error_isWarning_trackerError_returnsFalse() {
        assertThat(Torrent.Error.TRACKER_ERROR.isWarning()).isFalse()
    }

    @Test
    fun isActive_withPeersGettingFromUs_returnsTrue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(5)
            .peersSendingToUs(0)
            .webseedsSendingToUs(0)
            .status(Torrent.Status.STOPPED.value)
            .build()

        assertThat(torrent.isActive()).isTrue()
    }

    @Test
    fun isActive_withPeersSendingToUs_returnsTrue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(0)
            .peersSendingToUs(10)
            .webseedsSendingToUs(0)
            .status(Torrent.Status.STOPPED.value)
            .build()

        assertThat(torrent.isActive()).isTrue()
    }

    @Test
    fun isActive_withWebseeds_returnsTrue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(0)
            .peersSendingToUs(0)
            .webseedsSendingToUs(2)
            .status(Torrent.Status.STOPPED.value)
            .build()

        assertThat(torrent.isActive()).isTrue()
    }

    @Test
    fun isActive_checking_returnsTrue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(0)
            .peersSendingToUs(0)
            .webseedsSendingToUs(0)
            .status(Torrent.Status.CHECK.value)
            .build()

        assertThat(torrent.isActive()).isTrue()
    }

    @Test
    fun isActive_downloading_returnsTrue() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(0)
            .peersSendingToUs(0)
            .webseedsSendingToUs(0)
            .status(Torrent.Status.DOWNLOAD.value)
            .build()

        assertThat(torrent.isActive()).isTrue()
    }

    @Test
    fun isActive_stoppedNoPeers_returnsFalse() {
        val torrent = Torrent.Builder()
            .peersGettingFromUs(0)
            .peersSendingToUs(0)
            .webseedsSendingToUs(0)
            .status(Torrent.Status.STOPPED.value)
            .build()

        assertThat(torrent.isActive()).isFalse()
    }

    @Test
    fun isChecking_checkStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.CHECK.value)
            .build()

        assertThat(torrent.isChecking()).isTrue()
    }

    @Test
    fun isChecking_otherStatus_returnsFalse() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.DOWNLOAD.value)
            .build()

        assertThat(torrent.isChecking()).isFalse()
    }

    @Test
    fun isSeeding_seedStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.SEED.value)
            .build()

        assertThat(torrent.isSeeding()).isTrue()
    }

    @Test
    fun isSeeding_seedWaitStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.SEED_WAIT.value)
            .build()

        assertThat(torrent.isSeeding()).isTrue()
    }

    @Test
    fun isDownloading_downloadStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.DOWNLOAD.value)
            .build()

        assertThat(torrent.isDownloading()).isTrue()
    }

    @Test
    fun isDownloading_downloadWaitStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.DOWNLOAD_WAIT.value)
            .build()

        assertThat(torrent.isDownloading()).isTrue()
    }

    @Test
    fun isPaused_stoppedStatus_returnsTrue() {
        val torrent = Torrent.Builder()
            .status(Torrent.Status.STOPPED.value)
            .build()

        assertThat(torrent.isPaused()).isTrue()
    }

    @Test
    fun isCompleted_noRemainingAndHasSize_returnsTrue() {
        val torrent = Torrent.Builder()
            .leftUntilDone(0)
            .sizeWhenDone(1000000)
            .build()

        assertThat(torrent.isCompleted()).isTrue()
    }

    @Test
    fun isCompleted_hasRemaining_returnsFalse() {
        val torrent = Torrent.Builder()
            .leftUntilDone(500000)
            .sizeWhenDone(1000000)
            .build()

        assertThat(torrent.isCompleted()).isFalse()
    }

    @Test
    fun isCompleted_zeroSize_returnsFalse() {
        val torrent = Torrent.Builder()
            .leftUntilDone(0)
            .sizeWhenDone(0)
            .build()

        assertThat(torrent.isCompleted()).isFalse()
    }

    @Test
    fun getActivity_combinesDownloadAndUploadRates() {
        val torrent = Torrent.Builder()
            .downloadRate(1024)
            .uploadRate(512)
            .build()

        assertThat(torrent.activity).isEqualTo(1536)
    }

    @Test
    fun parcelable_writeAndRead_preservesAllData() {
        val original = Torrent.Builder()
            .id(12345)
            .hash("test_hash_12345")
            .name("Parcelable Test")
            .addedDate(1609459200)
            .totalSize(1073741824)
            .percentDone(0.75)
            .status(Torrent.Status.DOWNLOAD.value)
            .downloadRate(102400)
            .uploadRate(51200)
            .eta(3600)
            .uploadedSize(204800)
            .uploadRatio(2.0)
            .errorId(0)
            .errorString("No error")
            .isFinished(false)
            .sizeWhenDone(1073741824)
            .leftUntilDone(268435456)
            .peersGettingFromUs(5)
            .peersSendingToUs(10)
            .webseedsSendingToUs(2)
            .queuePosition(1)
            .recheckProgress(0.5)
            .doneDate(1609545600)
            .activityDate(1609459200)
            .build()

        // Write to parcel
        val parcel = Parcel.obtain()
        original.writeToParcel(parcel, 0)

        // Reset parcel position for reading
        parcel.setDataPosition(0)

        // Read from parcel
        val restored = Torrent.CREATOR.createFromParcel(parcel)

        // Verify all fields
        assertThat(restored.id).isEqualTo(original.id)
        assertThat(restored.hash).isEqualTo(original.hash)
        assertThat(restored.name).isEqualTo(original.name)
        assertThat(restored.addedDate).isEqualTo(original.addedDate)
        assertThat(restored.totalSize).isEqualTo(original.totalSize)
        assertThat(restored.percentDone).isWithin(0.001).of(original.percentDone)
        assertThat(restored.status).isEqualTo(original.status)
        assertThat(restored.downloadRate).isEqualTo(original.downloadRate)
        assertThat(restored.uploadRate).isEqualTo(original.uploadRate)
        assertThat(restored.eta).isEqualTo(original.eta)
        assertThat(restored.uploadedSize).isEqualTo(original.uploadedSize)
        assertThat(restored.uploadRatio).isWithin(0.001).of(original.uploadRatio)
        assertThat(restored.errorId).isEqualTo(original.errorId)
        assertThat(restored.errorMessage).isEqualTo(original.errorMessage)
        assertThat(restored.isFinished).isEqualTo(original.isFinished)
        assertThat(restored.sizeWhenDone).isEqualTo(original.sizeWhenDone)
        assertThat(restored.leftUntilDone).isEqualTo(original.leftUntilDone)
        assertThat(restored.peersGettingFromUs).isEqualTo(original.peersGettingFromUs)
        assertThat(restored.peersSendingToUs).isEqualTo(original.peersSendingToUs)
        assertThat(restored.webseedsSendingToUs).isEqualTo(original.webseedsSendingToUs)
        assertThat(restored.queuePosition).isEqualTo(original.queuePosition)
        assertThat(restored.recheckProgress).isWithin(0.001).of(original.recheckProgress)
        assertThat(restored.doneDate).isEqualTo(original.doneDate)
        assertThat(restored.activityDate).isEqualTo(original.activityDate)

        parcel.recycle()
    }

    @Test
    fun toString_includesHashField() {
        val torrent = Torrent.Builder()
            .hash("test_hash_toString")
            .name("ToString Test")
            .build()

        val toString = torrent.toString()

        assertThat(toString).contains("hash='test_hash_toString'")
        assertThat(toString).contains("name='ToString Test'")
    }
}
