package com.shareconnect.utorrentconnect.transport.request;

import android.util.Log;

import com.shareconnect.utorrentconnect.model.json.Torrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public abstract class TorrentActionRequest extends Request<Void> {

    private static final String TAG = TorrentActionRequest.class.getSimpleName();

    private String method;
    private int[] torrentIds;
    private String[] torrentHashes; // uTorrent uses hash strings

    public TorrentActionRequest(String method, int... torrentIds) {
        super(Void.class);
        this.method = method;
        this.torrentIds = torrentIds;
    }

    public TorrentActionRequest(String method, String... torrentHashes) {
        super(Void.class);
        this.method = method;
        this.torrentHashes = torrentHashes;
        // Create dummy IDs for compatibility
        this.torrentIds = new int[torrentHashes.length];
        for (int i = 0; i < torrentHashes.length; i++) {
            this.torrentIds[i] = torrentHashes[i].hashCode();
        }
    }

    @Override
    protected String getMethod() {
        return method;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray ids = new JSONArray();
        for (int id : torrentIds) {
            ids.put(id);
        }

        try {
            args.put("ids", ids);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to form arguments JSON object for '" + getMethod() + "' request", e);
        }
        return args;
    }

    public static int[] toIds(Collection<Torrent> torrents) {
        int[] ids = new int[torrents.size()];
        int i = 0;
        for (Torrent torrent : torrents) {
            ids[i++] = torrent.getId();
        }
        return ids;
    }

    /**
     * Extract hash strings from torrent collection.
     * Used for uTorrent Web API which requires hash-based identification.
     */
    public static String[] toHashes(Collection<Torrent> torrents) {
        String[] hashes = new String[torrents.size()];
        int i = 0;
        for (Torrent torrent : torrents) {
            // Use hash if available, otherwise fall back to ID
            String hash = torrent.getHash();
            hashes[i++] = (hash != null && !hash.isEmpty()) ? hash : String.valueOf(torrent.getId());
        }
        return hashes;
    }

    @Override
    public java.util.Map<String, String> getQueryParameters() {
        // uTorrent Web API: GET /gui/?token=TOKEN&action=ACTION&hash=HASH
        // uTorrent uses hash strings as primary identifiers

        java.util.Map<String, String> params = new java.util.HashMap<>();

        // Map Transmission methods to uTorrent actions
        String action = mapMethodToAction(method);
        params.put("action", action);

        // Use hash strings if available, otherwise fall back to IDs
        if (torrentHashes != null && torrentHashes.length > 0) {
            // Use first hash (for multiple torrents, would need URL builder support)
            params.put("hash", torrentHashes[0]);
        } else if (torrentIds != null && torrentIds.length > 0) {
            // Fallback to ID as hash (for backward compatibility)
            params.put("hash", String.valueOf(torrentIds[0]));
        }

        // TODO: Support multiple torrents with repeated hash parameters
        // uTorrent API format: &hash=HASH1&hash=HASH2&hash=HASH3
        // This requires OkHttpTransportManager to support adding multiple values for same key

        return params;
    }

    private String mapMethodToAction(String transmissionMethod) {
        // Map Transmission RPC methods to uTorrent actions
        switch (transmissionMethod) {
            case "torrent-start":
            case "torrent-start-now":
                return "start";
            case "torrent-stop":
                return "stop";
            case "torrent-verify":
                return "recheck";
            case "torrent-reannounce":
                return "recheck"; // uTorrent doesn't have direct equivalent
            default:
                // Try to extract action from method name
                String[] parts = transmissionMethod.split("-");
                if (parts.length > 1) {
                    return parts[1]; // e.g., "torrent-pause" -> "pause"
                }
                return transmissionMethod;
        }
    }
}
