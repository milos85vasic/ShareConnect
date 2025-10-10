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
