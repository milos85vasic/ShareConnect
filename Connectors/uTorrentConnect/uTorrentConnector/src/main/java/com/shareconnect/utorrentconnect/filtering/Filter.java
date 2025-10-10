package com.shareconnect.utorrentconnect.filtering;

import com.shareconnect.utorrentconnect.model.json.Torrent;

import java.util.function.Predicate;

public interface Filter extends Predicate<Torrent> {

    int getNameResId();

    int getEmptyMessageResId();
}
