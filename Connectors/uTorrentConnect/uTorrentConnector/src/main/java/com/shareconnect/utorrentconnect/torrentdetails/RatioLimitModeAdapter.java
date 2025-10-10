package com.shareconnect.utorrentconnect.torrentdetails;

import com.shareconnect.utorrentconnect.model.limitmode.LimitMode;
import com.shareconnect.utorrentconnect.model.limitmode.RatioLimitMode;

public class RatioLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return RatioLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return RatioLimitMode.values()[position];
    }
}
