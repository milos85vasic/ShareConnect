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
