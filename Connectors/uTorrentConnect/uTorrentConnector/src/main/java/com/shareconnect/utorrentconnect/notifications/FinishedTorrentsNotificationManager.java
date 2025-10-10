package com.shareconnect.utorrentconnect.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.shareconnect.utorrentconnect.MainActivity;
import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.preferences.NotificationsPreferencesActivity;
import com.shareconnect.utorrentconnect.preferences.PreferencesActivity;
import com.shareconnect.utorrentconnect.server.Server;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FinishedTorrentsNotificationManager {

    private static final int NOTIFICATION_ID_TORRENT_FINISHED = 1001;

    private final Context context;
    private final uTorrentRemote app;
    private final NotificationManager notificationManager;
    private final FinishedTorrentsDetector finishedTorrentsDetector;

    public FinishedTorrentsNotificationManager(Context context) {
        this.context = context;
        app = uTorrentRemote.getApplication(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        finishedTorrentsDetector = new FinishedTorrentsDetector();
    }

    public void checkForFinishedTorrents(Server server, List<Torrent> torrents) {
        Collection<Torrent> torrentsToNotify = finishedTorrentsDetector.filterFinishedTorrentsToNotify(torrents, server);
        if (!torrentsToNotify.isEmpty()) {
            showFinishedNotification(torrentsToNotify);
        }

        long lastFinishedDate = finishedTorrentsDetector.findLastFinishedDate(torrents);
        if (lastFinishedDate > server.getLastUpdateDate()) {
            server.setLastUpdateDate(lastFinishedDate);
            app.persistServers();
        } else if (lastFinishedDate <= 0) {
            server.setLastUpdateDate(1L);
            app.persistServers();
        }
    }

    private void showFinishedNotification(Collection<Torrent> finishedTorrents) {
        if (!notificationManager.areNotificationsEnabled()) {
            return;
        }

        final int count = finishedTorrents.size();
        String title = context.getResources().getQuantityString(R.plurals.torrents_finished, count, count);
        final String text = finishedTorrents.stream()
                .limit(5)
                .map(Torrent::getName)
                .collect(Collectors.joining(", "));
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                uTorrentRemote.NOTIFICATION_CHANNEL_ID
        );
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        for (Torrent torrent : finishedTorrents) {
            inboxStyle.addLine(torrent.getName());
        }
        builder.setStyle(inboxStyle);

        int flags;
        if (Build.VERSION.SDK_INT >= 31) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                flags
        );
        builder.setContentIntent(contentPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(new Intent(context, PreferencesActivity.class));
        stackBuilder.addNextIntent(new Intent(context, NotificationsPreferencesActivity.class));
        PendingIntent preferencesPendingIntent = stackBuilder.getPendingIntent(0, flags);
        builder.addAction(R.drawable.ic_settings, context.getString(R.string.notification_settings), preferencesPendingIntent);

        notificationManager.notify(NOTIFICATION_ID_TORRENT_FINISHED, builder.build());
    }
}
