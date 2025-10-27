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

import static java.util.Optional.ofNullable;

import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;

import com.shareconnect.utorrentconnect.model.json.Torrent;

import java.util.Comparator;

public enum SortedBy {

    NAME(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            String name1 = ofNullable(t1.getName()).orElse("");
            String name2 = ofNullable(t2.getName()).orElse("");
            return name1.compareToIgnoreCase(name2);
        }
    }),

    SIZE(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t1.getTotalSize(), t2.getTotalSize())
                    .compare(t1, t2, NAME.comparator)
                    .result();
        }
    }),

    TIME_REMAINING(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compareFalseFirst(t1.isCompleted(), t2.isCompleted())
                    .compareFalseFirst(t1.getEta() < 0, t2.getEta() < 0)
                    .compare(t1.getEta(), t2.getEta())
                    .result();
        }
    }),

    DATE_ADDED(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getAddedDate(), t1.getAddedDate())
                    .compare(t1, t2, QUEUE_POSITION.comparator)
                    .result();
        }
    }),

    PROGRESS(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t1.getPercentDone(), t2.getPercentDone())
                    .compare(t1, t2, UPLOAD_RATIO.comparator)
                    .result();
        }
    }),

    QUEUE_POSITION(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Ints.compare(t1.getQueuePosition(), t2.getQueuePosition());
        }
    }),

    UPLOAD_RATIO(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getUploadRatio(), t1.getUploadRatio())
                    .compare(t1, t2, STATE.comparator)
                    .result();
        }
    }),

    ACTIVITY(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getActivity(), t1.getActivity())
                    .compare(t1, t2, STATE.comparator)
                    .result();
        }
    }),

    STATE(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getStatus().value, t1.getStatus().value)
                    .compare(t1, t2, QUEUE_POSITION.comparator)
                    .result();
        }
    }),

    LAST_ACTIVITY(new Comparator<>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return ComparisonChain.start()
                    .compare(t2.getActivityDate(), t1.getActivityDate())
                    .compare(t2.getAddedDate(), t2.getAddedDate())
                    .result();
        }
    });

    private final Comparator<Torrent> comparator;

    SortedBy(Comparator<Torrent> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Torrent> getComparator() {
        return comparator;
    }
}
