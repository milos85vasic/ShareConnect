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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;

import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.server.Server;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

public class FinishedTorrentsDetectorTest {

    private FinishedTorrentsDetector detector;

    @Before
    public void setup() {
        detector = new FinishedTorrentsDetector();
    }

    @Test
    public void testFindLastFinishedDate() {
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        assertThat(detector.findLastFinishedDate(torrents)).isEqualTo(873L);
    }

    @Test
    public void testFindLastFinishedDateWithEmptyList() {
        assertThat(detector.findLastFinishedDate(Collections.emptyList())).isEqualTo(-1L);
    }

    @Test
    public void testFilterFinishedTorrentsToNotifyFirstUpdate() {
        Server server = new Server("test", "localhost", 9091);
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        Collection<Torrent> filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server);

        assertThat(filteredTorrents).isEmpty();
    }

    @Test
    public void testFilterFinishedTorrentsToNotify() {
        Server server = new Server("test", "localhost", 9091);
        server.setLastUpdateDate(100L);
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        Torrent[] filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server).toArray(new Torrent[0]);

        assertThat(filteredTorrents).hasLength(2);
        assertThat(filteredTorrents[0].getDoneDate()).isEqualTo(555L);
        assertThat(filteredTorrents[1].getDoneDate()).isEqualTo(873L);
    }
}
