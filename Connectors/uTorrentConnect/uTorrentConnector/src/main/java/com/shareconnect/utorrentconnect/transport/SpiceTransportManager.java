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


package com.shareconnect.utorrentconnect.transport;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.octo.android.robospice.retry.RetryPolicy;

import com.shareconnect.utorrentconnect.server.Server;
import com.shareconnect.utorrentconnect.transport.request.Request;

import javax.annotation.Nullable;

import roboguice.util.temp.Ln;

public class SpiceTransportManager extends SpiceManager implements TransportManager {

    private static final String TAG = SpiceTransportManager.class.getSimpleName();

    private static final RetryPolicy RETRY_POLICY_NO_RETRIES = new DefaultRetryPolicy(0, 0L, 0);

    private Server currentServer;

    public SpiceTransportManager() {
        super(NoCacheGoogleHttpClientSpiceService.class);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    public void setServer(@Nullable Server server) {
        currentServer = server;
    }

    private <T> void doRequest(final Request<T> request, @NonNull Server server, @Nullable final RequestListener<T> listener) {
        request.setServer(server);
        request.setRetryPolicy(RETRY_POLICY_NO_RETRIES);

        Log.d(TAG, "execute " + request.getClass().getSimpleName());
        execute(request, new RetryPropagateRequestListener<>(request, listener) {
            @Override
            protected void retry(Request<T> request, @Nullable RequestListener<T> listener) {
                doRequest(request, request.getServer(), listener);
            }
        });
    }

    public <T> void doRequest(@NonNull Request<T> request, RequestListener<T> listener) {
        if (currentServer == null)
            throw new IllegalStateException("Trying to send request while there is no active server");
        doRequest(request, currentServer, listener);
    }

    @Override
    public <T> void doRequest(@NonNull final Request<T> request, final RequestListener<T> listener, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> doRequest(request, listener),
                delay
        );
    }
}
