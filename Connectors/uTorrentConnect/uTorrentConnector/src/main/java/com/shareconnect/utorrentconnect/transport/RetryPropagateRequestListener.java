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

import android.util.Log;

import com.google.api.client.http.HttpStatusCodes;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.transport.request.Request;

import java.net.HttpURLConnection;

import javax.annotation.Nullable;

public abstract class RetryPropagateRequestListener<T> extends PropagateRequestListener<T> {

    private static final String TAG = RetryPropagateRequestListener.class.getSimpleName();

    private Request<T> request;
    private RequestListener<T> listener;

    public RetryPropagateRequestListener(Request<T> request,  @Nullable RequestListener<T> listener) {
        super(listener);
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected boolean onSuccess(T t) {
        return true;
    }

    @Override
    protected boolean onFailure(SpiceException spiceException) {
        int statusCode = request.getResponseStatusCode();
        if (statusCode == HttpURLConnection.HTTP_CONFLICT) {
            Log.d(TAG, "SC_CONFLICT old sessionId: " + request.getServer().getLastSessionId());
            String responseSessionId = request.getResponseSessionId();
            Log.d(TAG, "new sessionId: " + responseSessionId);
            request.getServer().setLastSessionId(responseSessionId);
            retry(request, listener);
            return false;
        } else if (HttpStatusCodes.isRedirect(statusCode)) {
            request.getServer().setRedirectLocation(request.getRedirectLocation());
            retry(request, listener);
            return false;
        }
        return true;
    }

    protected abstract void retry(Request<T> request, @Nullable RequestListener<T> listener);
}
