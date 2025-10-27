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

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.DefaultRequestRunner;
import com.octo.android.robospice.request.RequestProcessorListener;
import com.octo.android.robospice.request.RequestProgressManager;
import com.octo.android.robospice.request.RequestRunner;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.notifier.DefaultRequestListenerNotifier;
import com.octo.android.robospice.request.notifier.SpiceServiceListenerNotifier;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.octo.android.robospice.retry.RetryPolicy;

import com.shareconnect.utorrentconnect.server.Server;
import com.shareconnect.utorrentconnect.transport.request.Request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequestExecutor {

    private static final RetryPolicy NO_RETRY_POLICY = new DefaultRetryPolicy(0, 0, 0);

    private HttpRequestFactory httpRequestFactory = AndroidHttp.newCompatibleTransport().createRequestFactory();
    private final Map<CachedSpiceRequest<?>, Set<RequestListener<?>>> requestListenersMap = new HashMap<>();
    private final RequestRunner requestRunner;

    public RequestExecutor(Context context) {
        this(context, new DefaultNetworkStateChecker());
    }

    RequestExecutor(Context context, @Nonnull NetworkStateChecker networkStateChecker) {

        RequestProgressManager requestProgressManager = new RequestProgressManager(new RequestProcessorListener() {
            @Override
            public void requestsInProgress() {
            }

            @Override
            public void allRequestComplete() {
                unregisterAllListeners();
            }
        }, new DefaultRequestListenerNotifier(), new SpiceServiceListenerNotifier());

        requestRunner = new DefaultRequestRunner(context,
                new CacheManager() {
                    @Override public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                        return data;
                    }
                },
                Executors.newSingleThreadExecutor(),
                requestProgressManager,
                networkStateChecker
        );

        requestProgressManager.setMapRequestToRequestListener(requestListenersMap);
    }

    public <T> void executeRequest(@Nonnull Request<T> request, @Nonnull Server server, @Nullable RequestListener<T> listener) {
        request.setServer(server);
        request.setRetryPolicy(NO_RETRY_POLICY);
        request.setHttpRequestFactory(httpRequestFactory);

        CachedSpiceRequest<T> cachedSpiceRequest = new CachedSpiceRequest<>(request, null, DurationInMillis.ALWAYS_EXPIRED);

        registerListener(cachedSpiceRequest, new RetryPropagateRequestListener<T>(request, listener) {
            @Override
            protected void retry(Request<T> request, @Nullable RequestListener<T> listener) {
                executeRequest(request, request.getServer(), listener);
            }
        });

        requestRunner.executeRequest(cachedSpiceRequest);
    }

    private void registerListener(CachedSpiceRequest request, @Nonnull RequestListener listener) {
        Set<RequestListener<?>> listeners = requestListenersMap.get(request);
        if (listeners == null) {
            listeners = new HashSet<>();
            requestListenersMap.put(request, listeners);
        }
        listeners.add(listener);
    }

    public void unregisterAllListeners() {
        requestListenersMap.clear();
    }
}
