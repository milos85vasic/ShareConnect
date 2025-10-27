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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TorrentRemoveRequest extends Request<Void> {

    private static final String TAG = TorrentRemoveRequest.class.getSimpleName();
    private int[] ids;
    private boolean deleteLocalData;

    public TorrentRemoveRequest(int[] ids, boolean deleteLocalData) {
        super(Void.class);
        this.ids = ids;
        this.deleteLocalData = deleteLocalData;
    }

    @Override
    protected String getMethod() {
        return "torrent-remove";
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray idArray = new JSONArray();
        for (int id : ids) {
            idArray.put(id);
        }
        try {
            args.put("ids", idArray);
            args.put("delete-local-data", deleteLocalData);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }

        return args;
    }

    @Override
    public java.util.Map<String, String> getQueryParameters() {
        // uTorrent Web API:
        // Remove without data: GET /gui/?token=TOKEN&action=remove&hash=HASH
        // Remove with data: GET /gui/?token=TOKEN&action=removedata&hash=HASH

        java.util.Map<String, String> params = new java.util.HashMap<>();

        // Choose action based on deleteLocalData flag
        String action = deleteLocalData ? "removedata" : "remove";
        params.put("action", action);

        // For now, use ID as hash (this will need proper implementation)
        // In real uTorrent API, we'd need the actual hash string
        if (ids != null && ids.length > 0) {
            params.put("hash", String.valueOf(ids[0]));
        }

        return params;
    }
}
