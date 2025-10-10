package com.shareconnect.utorrentconnect.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shareconnect.utorrentconnect.uTorrentRemote;

public class BootCompleteBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (uTorrentRemote.getApplication(context).isNotificationEnabled()) {
            BackgroundUpdater.start(context);
        }
    }
}
