package com.shareconnect.utorrentconnect.transport.request;

import com.shareconnect.utorrentconnect.model.json.PortTestResult;

import org.json.JSONObject;

public class PortTestRequest extends Request<PortTestResult> {

    public PortTestRequest() {
        super(PortTestResult.class);
    }

    @Override
    public String getMethod() {
        return "port-test";
    }

    @Override
    public JSONObject getArguments() {
        return null;
    }
}
