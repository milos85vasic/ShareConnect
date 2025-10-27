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

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import javax.annotation.Nullable;

public abstract class PropagateRequestListener<RESULT> implements RequestListener<RESULT> {

    private RequestListener<RESULT> listener;

    public PropagateRequestListener(@Nullable RequestListener<RESULT> listener) {
        this.listener = listener;
    }

    @Override
    public final void onRequestFailure(SpiceException spiceException) {
        boolean propagate = onFailure(spiceException);
        if (propagate && listener != null) {
            listener.onRequestFailure(spiceException);
        }
    }

    @Override
    public final void onRequestSuccess(RESULT result) {
        boolean propagate = onSuccess(result);
        if (propagate && listener != null) {
            listener.onRequestSuccess(result);
        }
    }

    /**
     * Called when request failure notification received.
     * Depending on return value {@code listener} is notified or not.
     * @param spiceException exception with failure information
     * @return whether {@code listener} should be notified
     */
    protected abstract boolean onFailure(SpiceException spiceException);

    /**
     * Called when request success notification received.
     * Depending on return value {@code listener} is notified or not.
     * @param result request result
     * @return whether {@code listener} should be notified
     */
    protected abstract boolean onSuccess(RESULT result);
}
