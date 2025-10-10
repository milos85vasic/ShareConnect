package com.shareconnect.utorrentconnect.transport.request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public class AddTorrentByUrlRequest extends AddTorrentRequest {

    private static final String TAG = AddTorrentByUrlRequest.class.getSimpleName();

    private String url;

    public AddTorrentByUrlRequest(@Nonnull final String url, String destination, boolean paused) {
        super(destination, paused);

        if (url.matches("^[0-9a-fA-F]{40}$"))
            this.url = "magnet:?xt=urn:btih:"+url;
        else {
            this.url = url;
        }
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = super.getArguments();
        try {
            args.put("filename", url);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object");
        }
        return args;
    }

    @Override
    public java.util.Map<String, String> getQueryParameters() {
        // uTorrent Web API: GET /gui/?token=TOKEN&action=add-url&s=URL_OR_MAGNET&path=DOWNLOAD_PATH
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("action", "add-url");
        params.put("s", url);

        // Get destination from parent class via reflection or add getter
        // For now, we'll need to make destination accessible
        String dest = getDestination();
        if (dest != null && !dest.isEmpty()) {
            params.put("path", dest);
        }

        return params;
    }

    // Helper method to access parent's destination field
    private String getDestination() {
        try {
            java.lang.reflect.Field field = AddTorrentRequest.class.getDeclaredField("destination");
            field.setAccessible(true);
            return (String) field.get(this);
        } catch (Exception e) {
            Log.w(TAG, "Could not access destination field", e);
            return null;
        }
    }
}
