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

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.TrackerStats;

import java.util.Comparator;

public enum TrackersSortedBy {

    TIERS(R.string.trackers_sort_by_tiers, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            int x = p1.tier;
            int y = p2.tier;
            return (x < y) ? -1 : (x == y ? 0 : 1);
        }
    }),

    SEEDERS(R.string.trackers_sort_by_seeders, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.seederCount, p2.seederCount);
        }
    }),

    LEECHERS(R.string.trackers_sort_by_leechers, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.leecherCount, p2.leecherCount);
        }
    }),

    DOWNLOADED(R.string.trackers_sort_by_downloaded, new Comparator<TrackerStats>() {
        @Override
        public int compare(TrackerStats p1, TrackerStats p2) {
            return Integer.compare(p1.downloadCount, p2.downloadCount);
        }
    });

    @StringRes
    public final int nameResId;
    public final Comparator<TrackerStats> comparator;

    TrackersSortedBy(@StringRes int nameResId, Comparator<TrackerStats> comparator) {
        this.nameResId = nameResId;
        this.comparator = comparator;
    }
}
