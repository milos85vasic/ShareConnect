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


package com.shareconnect.utorrentconnect.sorting;

import androidx.annotation.StringRes;

import com.google.common.base.Strings;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.Peer;

import java.util.Comparator;

public enum PeersSortedBy {

    ADDRESS(R.string.peers_sort_by_address, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            String address1 = Strings.nullToEmpty(p1.address);
            String address2 = Strings.nullToEmpty(p2.address);
            return address1.compareToIgnoreCase(address2);
        }
    }),

    CLIENT(R.string.peers_sort_by_client, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            String client1 = Strings.nullToEmpty(p1.clientName);
            String client2 = Strings.nullToEmpty(p2.clientName);
            return client1.compareToIgnoreCase(client2);
        }
    }),

    PROGRESS(R.string.peers_sort_by_progress, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Double.compare(p1.progress, p2.progress);
        }
    }),

    DOWNLOADED_RATE(R.string.peers_sort_by_download_rate, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Long.signum(p2.rateToClient - p1.rateToClient);
        }
    }),

    UPLOAD_RATE(R.string.peers_sort_by_upload_rate, new Comparator<Peer>() {
        @Override
        public int compare(Peer p1, Peer p2) {
            return Long.signum(p2.rateToPeer - p1.rateToPeer);
        }
    });

    @StringRes public final int nameResId;
    public final Comparator<Peer> comparator;

    PeersSortedBy(@StringRes int nameResId, Comparator<Peer> comparator) {
        this.nameResId = nameResId;
        this.comparator = comparator;
    }
}
