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

import com.shareconnect.utorrentconnect.model.TorrentMetadata;
import com.shareconnect.utorrentconnect.model.json.Torrents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TorrentGetRequest extends Request<Torrents> {

    private static final String TAG = TorrentGetRequest.class.getSimpleName();

    private static final List<String> FIELD_KEYS = Arrays.asList(
            TorrentMetadata.ID,
            TorrentMetadata.NAME,
            TorrentMetadata.PERCENT_DONE,
            TorrentMetadata.TOTAL_SIZE,
            TorrentMetadata.ADDED_DATE,
            TorrentMetadata.STATUS,
            TorrentMetadata.RATE_DOWNLOAD,
            TorrentMetadata.RATE_UPLOAD,
            TorrentMetadata.UPLOADED_EVER,
            TorrentMetadata.UPLOAD_RATIO,
            TorrentMetadata.ETA,
            TorrentMetadata.ERROR,
            TorrentMetadata.ERROR_STRING,
            TorrentMetadata.IS_FINISHED,
            TorrentMetadata.SIZE_WHEN_DONE,
            TorrentMetadata.LEFT_UNTIL_DONE,
            TorrentMetadata.PEERS_GETTING_FROM_US,
            TorrentMetadata.PEERS_SENDING_TO_US,
            TorrentMetadata.WEBSEEDS_SENDING_TO_US,
            TorrentMetadata.QUEUE_POSITION,
            TorrentMetadata.RECHECK_PROGRESS,
            TorrentMetadata.DONE_DATE,
            TorrentMetadata.ACTIVITY_DATE
    );

    private final JSONObject args;

    public TorrentGetRequest() {
        super(Torrents.class);
        try {
            args = new JSONObject().put("fields", new JSONArray(FIELD_KEYS));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            throw new RuntimeException(e);
        }
    }

    public TorrentGetRequest(int... ids) {
        this();

        JSONArray idsArray = new JSONArray();
        for (int id : ids) {
            idsArray.put(id);
        }
        try {
            args.put("ids", idsArray);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        return args;
    }

    @Override
    public java.util.Map<String, String> getQueryParameters() {
        // uTorrent Web API: GET /gui/?token=TOKEN&list=1
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("list", "1");
        return params;
    }
}
