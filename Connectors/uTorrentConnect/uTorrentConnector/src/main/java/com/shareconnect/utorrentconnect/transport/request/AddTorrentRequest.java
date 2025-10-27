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


package com.shareconnect.utorrentconnect.transport.request;

import android.util.Log;

import com.shareconnect.utorrentconnect.model.json.AddTorrentResult;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AddTorrentRequest extends Request<AddTorrentResult> {

    private static final String TAG = AddTorrentRequest.class.getSimpleName();

    private String destination;
    private boolean paused;

    public AddTorrentRequest(String destination, boolean paused) {
        super(AddTorrentResult.class);
        this.destination = destination;
        this.paused = paused;
    }

    @Override
    protected String getMethod() {
        return "torrent-add";
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();
        try {
            if (destination != null) args.put("download-dir", destination);
            args.put("paused", paused);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object");
        }

        return args;
    }
}
