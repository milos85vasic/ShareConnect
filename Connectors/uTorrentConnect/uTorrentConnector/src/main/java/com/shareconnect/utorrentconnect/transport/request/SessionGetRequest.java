package com.shareconnect.utorrentconnect.transport.request;

import com.shareconnect.utorrentconnect.model.json.ServerSettings;

import org.json.JSONObject;

public class SessionGetRequest extends Request<ServerSettings> {

    public SessionGetRequest() {
        super(ServerSettings.class);
    }

    @Override
    protected String getMethod() {
        return "session-get";
    }

    @Override
    protected JSONObject getArguments() {
        return null;
    }

    @Override
    public java.util.Map<String, String> getQueryParameters() {
        // uTorrent Web API: GET /gui/?token=TOKEN&action=getsettings
        java.util.Map<String, String> params = new java.util.HashMap<>();
        params.put("action", "getsettings");
        return params;
    }
}
