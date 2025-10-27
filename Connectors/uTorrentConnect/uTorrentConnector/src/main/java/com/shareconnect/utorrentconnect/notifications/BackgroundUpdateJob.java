/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.utorrentconnect.notifications;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.model.json.Torrents;
import com.shareconnect.utorrentconnect.server.Server;
import com.shareconnect.utorrentconnect.transport.RequestExecutor;
import com.shareconnect.utorrentconnect.transport.request.TorrentGetRequest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BackgroundUpdateJob extends Job {

    private static final String TAG_UPDATE_TORRENTS = "tag_update_torrents";
    private static final String TAG = BackgroundUpdateJob.class.getSimpleName();

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Context context = getContext();

        List<Server> servers = uTorrentRemote.getApplication(context).getServers();
        final CountDownLatch countDownLatch = new CountDownLatch(servers.size());
        final RequestExecutor requestExecutor = new RequestExecutor(context);
        final FinishedTorrentsNotificationManager finishedTorrentsNotificationManager = new FinishedTorrentsNotificationManager(context);

        for (final Server server : servers) {
            requestExecutor.executeRequest(new TorrentGetRequest(), server, new RequestListener<Torrents>() {
                @Override
                public void onRequestSuccess(Torrents torrents) {
                    finishedTorrentsNotificationManager.checkForFinishedTorrents(server, torrents);
                    countDownLatch.countDown();
                }

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Failed to retrieve torrent list from " + server.getName() + "(" + server.getHost() + ")");
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            requestExecutor.unregisterAllListeners();
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    public static void schedule(boolean onlyUnmeteredNetwork) {
        Set<JobRequest> pendingJobs = JobManager.instance().getAllJobRequestsForTag(TAG_UPDATE_TORRENTS);
        if (pendingJobs.size() > 0) {
            return; // Already scheduled
        }

        JobRequest.Builder builder = new JobRequest.Builder(TAG_UPDATE_TORRENTS)
                .setPeriodic(JobRequest.MIN_INTERVAL, (long) (0.75 * JobRequest.MIN_INTERVAL));

        if (onlyUnmeteredNetwork) {
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
        } else {
            builder.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED);
        }

        builder.setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    public static void cancelAll() {
        JobManager.instance().cancelAllForTag(TAG_UPDATE_TORRENTS);
    }

    public static class Creator implements JobCreator {

        @Nullable
        @Override
        public Job create(@NonNull String tag) {
            switch (tag) {
                case TAG_UPDATE_TORRENTS:
                    return new BackgroundUpdateJob();
                default:
                    return null;

            }
        }
    }
}
