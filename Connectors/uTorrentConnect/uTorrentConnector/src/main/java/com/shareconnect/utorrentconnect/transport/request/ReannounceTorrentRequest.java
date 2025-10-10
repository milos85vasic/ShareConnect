package com.shareconnect.utorrentconnect.transport.request;

public class ReannounceTorrentRequest extends TorrentActionRequest {

    public ReannounceTorrentRequest(int... torrentIds) {
        super("torrent-reannounce", torrentIds);
    }
}
