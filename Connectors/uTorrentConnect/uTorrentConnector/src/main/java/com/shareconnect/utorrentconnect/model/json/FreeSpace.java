package com.shareconnect.utorrentconnect.model.json;

import com.google.api.client.util.Key;

public class FreeSpace {

    @Key("size-bytes") private long size;

    public long getSizeInBytes() {
        return size;
    }
}
