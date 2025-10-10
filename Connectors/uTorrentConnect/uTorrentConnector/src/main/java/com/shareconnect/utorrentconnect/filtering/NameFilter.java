package com.shareconnect.utorrentconnect.filtering;

import androidx.annotation.NonNull;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.Torrent;

public class NameFilter extends BaseFilter {

    private String query;

    public NameFilter() {
        super(R.string.filter_name, R.string.filter_empty_name);
    }

    public NameFilter withQuery(@NonNull String query) {
        this.query = query.toLowerCase();
        return this;
    }

    @Override
    public boolean test(Torrent torrent) {
        return torrent.getName().toLowerCase().contains(query);
    }
}
