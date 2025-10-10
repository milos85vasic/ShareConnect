package com.shareconnect.utorrentconnect.transport.request;

public class VerifyTorrentRequest extends TorrentActionRequest {

    public VerifyTorrentRequest(int... torrentIds) {
        super("torrent-verify", torrentIds);
    }
}
