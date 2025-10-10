package com.shareconnect.utorrentconnect.torrentdetails;

import com.shareconnect.utorrentconnect.model.limitmode.IdleLimitMode;
import com.shareconnect.utorrentconnect.model.limitmode.LimitMode;

public class IdleLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return IdleLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return IdleLimitMode.values()[position];
    }
}
