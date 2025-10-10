package com.shareconnect.utorrentconnect.transport.request;

import com.shareconnect.utorrentconnect.model.json.Torrent;

import java.util.Collection;

public class StopTorrentRequest extends TorrentActionRequest {

    public StopTorrentRequest(int... torrentIds) {
        super("torrent-stop", torrentIds);
    }

    public StopTorrentRequest(Collection<Torrent> torrents) {
        this(toIds(torrents));
    }
}
