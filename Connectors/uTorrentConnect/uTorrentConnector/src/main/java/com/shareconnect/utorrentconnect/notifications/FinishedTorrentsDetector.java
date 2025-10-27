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


package com.shareconnect.utorrentconnect.notifications;

import com.google.common.base.Predicate;

import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.server.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import static com.google.common.collect.FluentIterable.from;

public class FinishedTorrentsDetector {

    public Collection<Torrent> filterFinishedTorrentsToNotify(Collection<Torrent> torrents, Server server) {
        long lastUpdateDate = server.getLastUpdateDate();
        if (lastUpdateDate <= 0) return Collections.emptySet();

        return filterFinishedAfterDate(torrents, lastUpdateDate);
    }

    /**
     * @return date of last finished torrent or {@code -1} if list is empty
     */
    public long findLastFinishedDate(Collection<Torrent> torrents) {
        if (torrents.isEmpty()) return -1;

        List<Torrent> sortedTorrents = sortByDoneDate(torrents);
        return sortedTorrents.get(0).getDoneDate();
    }

    private Collection<Torrent> filterFinishedAfterDate(Collection<Torrent> torrents, final long date) {
        return from(torrents).filter(new Predicate<Torrent>() {
            @Override
            public boolean apply(@Nonnull Torrent torrent) {
                return torrent.getDoneDate() > date;
            }
        }).toSet();
    }

    private List<Torrent> sortByDoneDate(Collection<Torrent> torrents) {
        return from(torrents).toSortedList(Collections.reverseOrder(new Comparator<Torrent>() {
            @Override
            public int compare(Torrent t1, Torrent t2) {
                return compare(t1.getDoneDate(), t2.getDoneDate());
            }

            private int compare(long x, long y) {
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        }));
    }
}
