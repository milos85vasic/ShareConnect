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
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TorrentInfoGetRequest extends Request<TorrentInfo> {

    private static final String TAG = TorrentInfoGetRequest.class.getSimpleName();

    private static final List<String> FIELD_KEYS = Arrays.asList(
            TorrentMetadata.ID,
            TorrentMetadata.FILES,
            TorrentMetadata.FILE_STATS,
            TorrentMetadata.BANDWIDTH_PRIORITY,
            TorrentMetadata.HONORS_SESSION_LIMITS,
            TorrentMetadata.DOWNLOAD_LIMITED,
            TorrentMetadata.DOWNLOAD_LIMIT,
            TorrentMetadata.UPLOAD_LIMITED,
            TorrentMetadata.UPLOAD_LIMIT,
            TorrentMetadata.SEED_RATIO_LIMIT,
            TorrentMetadata.SEED_RATIO_MODE,
            TorrentMetadata.SEED_IDLE_LIMIT,
            TorrentMetadata.SEED_IDLE_MODE,

            TorrentMetadata.HAVE_UNCHECKED,
            TorrentMetadata.HAVE_VALID,
            TorrentMetadata.SIZE_WHEN_DONE,
            TorrentMetadata.LEFT_UNTIL_DONE,
            TorrentMetadata.DESIRED_AVAILABLE,
            TorrentMetadata.PIECE_COUNT,
            TorrentMetadata.PIECE_SIZE,
            TorrentMetadata.DOWNLOAD_DIR,
            TorrentMetadata.IS_PRIVATE,
            TorrentMetadata.CREATOR,
            TorrentMetadata.DATE_CREATED,
            TorrentMetadata.COMMENT,
            TorrentMetadata.DOWNLOAD_EVER,
            TorrentMetadata.CORRUPT_EVER,
            TorrentMetadata.UPLOADED_EVER,
            TorrentMetadata.ADDED_DATE,
            TorrentMetadata.SECONDS_DOWNLOADING,
            TorrentMetadata.SECONDS_SEEDING,
            TorrentMetadata.PEERS,
            TorrentMetadata.TRACKERS,
            TorrentMetadata.TRACKER_STATS,
            TorrentMetadata.MAGNET_LINK
    );

    private final int id;

    public TorrentInfoGetRequest(int id) {
        super(TorrentInfo.class);
        this.id = id;
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        try {
            return new JSONObject()
                    .put("ids", new JSONArray().put(id))
                    .put("fields", new JSONArray(FIELD_KEYS));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
