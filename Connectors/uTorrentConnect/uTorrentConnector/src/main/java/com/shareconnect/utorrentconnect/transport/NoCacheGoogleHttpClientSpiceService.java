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

import android.app.Application;
import android.app.Notification;
import android.util.Log;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.server.Server;
import com.shareconnect.utorrentconnect.transport.request.Request;

import java.security.GeneralSecurityException;
import java.util.Set;

public class NoCacheGoogleHttpClientSpiceService extends GoogleHttpClientSpiceService {

    private static final String TAG = NoCacheGoogleHttpClientSpiceService.class.getSimpleName();

    private HttpRequestFactory defaultHttpRequestFactory;
    private HttpRequestFactory trustAllHttpRequestFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        defaultHttpRequestFactory = httpRequestFactory;

        try {
            trustAllHttpRequestFactory = new NetHttpTransport.Builder()
                    .doNotValidateCertificate()
                    .build()
                    .createRequestFactory();
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error while creating HTTP request factory", e);
        }
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return new CacheManager() {
            @Override
            public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                return data;
            }
        };
    }

    @Override
    public Notification createDefaultNotification() {
        return null;
    }

    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof Request) {
            Server server = ((Request) request.getSpiceRequest()).getServer();
            httpRequestFactory = server.useHttps() && server.getTrustSelfSignedSslCert()
                    ? trustAllHttpRequestFactory
                    : defaultHttpRequestFactory;
        }
        super.addRequest(request, listRequestListener);
    }
}
