# uTorrent Web API Implementation Guide

## Overview

This document provides detailed guidance for implementing the uTorrent Web API protocol in the uTorrentConnect connector. The current codebase uses Transmission's JSON-RPC protocol and needs to be adapted to uTorrent's query parameter-based REST API.

## API Differences

### Transmission (Current)
- Protocol: JSON-RPC over HTTP POST
- Authentication: Session ID in header (`X-Transmission-Session-Id`)
- Request Format: JSON body with `method` and `arguments`
- Response Format: JSON with `result` and `arguments` fields

### uTorrent (Target)
- Protocol: REST API with query parameters over HTTP GET
- Authentication: Token-based (token fetched from `/gui/token.html`)
- Request Format: URL query parameters
- Response Format: JSON with various structures (arrays for efficiency)

## Implementation Steps

### 1. Token Management

#### Current: SessionIdInterceptor.kt
```kotlin
class SessionIdInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Adds X-Transmission-Session-Id header
    }
}
```

#### Target: TokenInterceptor.kt
```kotlin
class TokenInterceptor : Interceptor {
    private var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Check if token exists
        // 2. If not, fetch from /gui/token.html
        // 3. Parse HTML to extract token
        // 4. Add token as query parameter
        // 5. Handle 401 responses by refreshing token
    }
}
```

**Token Fetch Process:**
```http
GET /gui/token.html HTTP/1.1
Host: localhost:8080
Authorization: Basic dXNlcjpwYXNz

Response:
<html><div id='token'>TOKEN_VALUE_HERE</div></html>
```

### 2. Request Structure Changes

#### Current Request Flow (Transmission)
```json
POST /transmission/rpc
{
  "method": "torrent-get",
  "arguments": {
    "ids": [1, 2, 3],
    "fields": ["id", "name", "status"]
  }
}
```

#### Target Request Flow (uTorrent)
```http
GET /gui/?token=TOKEN&list=1 HTTP/1.1
```

### 3. Request Classes to Adapt

All request classes in `transport/request/` need modification:

#### TorrentGetRequest.java
**Current:** JSON-RPC method `torrent-get`
**Target:** GET `/gui/?token=TOKEN&list=1`

```java
// OLD
protected String getMethod() {
    return "torrent-get";
}

protected JSONObject getArguments() {
    JSONObject args = new JSONObject();
    args.put("fields", FIELDS_ARRAY);
    return args;
}

// NEW
protected String buildQueryParams() {
    return "list=1";
}
```

#### AddTorrentByUrlRequest.java
**Current:** JSON-RPC method `torrent-add`
**Target:** GET `/gui/?token=TOKEN&action=add-url&s=MAGNET_OR_URL`

```java
// NEW
protected String buildQueryParams() {
    String encodedUrl = URLEncoder.encode(torrentUrl, "UTF-8");
    StringBuilder params = new StringBuilder();
    params.append("action=add-url");
    params.append("&s=").append(encodedUrl);
    if (downloadDir != null) {
        params.append("&path=").append(URLEncoder.encode(downloadDir, "UTF-8"));
    }
    return params.toString();
}
```

#### TorrentRemoveRequest.java
**Current:** JSON-RPC method `torrent-remove`
**Target:** GET `/gui/?token=TOKEN&action=remove&hash=HASH`

#### StartTorrentRequest.java
**Current:** JSON-RPC method `torrent-start`
**Target:** GET `/gui/?token=TOKEN&action=start&hash=HASH`

#### StopTorrentRequest.java
**Current:** JSON-RPC method `torrent-stop`
**Target:** GET `/gui/?token=TOKEN&action=stop&hash=HASH`

### 4. Response Parsing Changes

#### uTorrent List Response Format
```json
{
  "build": 25460,
  "torrents": [
    [
      "HASH",           // 0: Hash
      201,              // 1: Status
      "Torrent Name",   // 2: Name
      1073741824,       // 3: Size
      750,              // 4: Progress (per mille)
      102400,           // 5: Downloaded
      204800,           // 6: Uploaded
      2048,             // 7: Ratio
      102400,           // 8: Upload Speed
      51200,            // 9: Download Speed
      3600,             // 10: ETA
      "Label",          // 11: Label
      5,                // 12: Peers Connected
      10,               // 13: Peers in Swarm
      3,                // 14: Seeds Connected
      5,                // 15: Seeds in Swarm
      1.990,            // 16: Availability
      1,                // 17: Queue Order
      102400            // 18: Remaining
    ]
  ],
  "torrentc": "CACHE_ID"
}
```

#### Field Mapping (Array Index → Property)
- 0: hash (String)
- 1: status (Int) - See status codes below
- 2: name (String)
- 3: size (Long)
- 4: progress (Int) - per mille (0-1000)
- 5: downloaded (Long)
- 6: uploaded (Long)
- 7: ratio (Int) - ratio * 1000
- 8: uploadSpeed (Long)
- 9: downloadSpeed (Long)
- 10: eta (Int) - seconds
- 11: label (String)
- 12: peersConnected (Int)
- 13: peersInSwarm (Int)
- 14: seedsConnected (Int)
- 15: seedsInSwarm (Int)
- 16: availability (Float)
- 17: queueOrder (Int)
- 18: remaining (Long)

#### Status Codes
```
1    = Started
2    = Checking
4    = Start after check
8    = Checked
16   = Error
32   = Paused
64   = Queued
128  = Loaded
```

Combine flags: e.g., `201 = Started + Checked + Loaded` (1 + 8 + 128 + 64)

### 5. OkHttpTransportManager Changes

#### Current Implementation
```kotlin
val okHttpRequest = okhttp3.Request.Builder()
    .post(request.createBody().toRequestBody(MEDIA_TYPE_JSON))
    .url(url)
    .build()
```

#### Target Implementation
```kotlin
// Build URL with query parameters
val urlWithParams = HttpUrl.Builder().apply {
    scheme(if (server.useHttps()) "https" else "http")
    host(server.host.trim('/', '\\'))
    port(server.port)
    addPathSegments("gui/")
    // Add token (handled by interceptor)
    // Add request-specific params
    request.getQueryParams().forEach { (key, value) ->
        addQueryParameter(key, value)
    }
}.build()

val okHttpRequest = okhttp3.Request.Builder()
    .get()  // Changed from POST to GET
    .url(urlWithParams)
    .build()
```

### 6. Error Handling

uTorrent may return HTTP error codes or empty responses:
- 400: Invalid request
- 401: Invalid token (fetch new token)
- 404: Torrent not found

### 7. Testing Requirements

#### Unit Tests
- Token fetching and parsing
- Query parameter building
- Response array parsing
- Status code interpretation
- Error handling

#### Integration Tests
- Full request/response cycle with mock server
- Token refresh on 401
- All torrent operations

#### Mock Server Updates
Update `/mockserver/` to simulate uTorrent API:
- `/gui/token.html` endpoint
- `/gui/?list=1` endpoint
- Various action endpoints

## API Endpoint Reference

### Core Endpoints

#### Get Token
```
GET /gui/token.html
Response: <html><div id='token'>TOKEN</div></html>
```

#### List Torrents
```
GET /gui/?token=TOKEN&list=1
Optional: &cid=CACHE_ID (only get updates since cache ID)
```

#### Add Torrent by URL
```
GET /gui/?token=TOKEN&action=add-url&s=URL_OR_MAGNET
Optional: &path=DOWNLOAD_PATH
```

#### Add Torrent by File
```
POST /gui/?token=TOKEN&action=add-file
Content-Type: multipart/form-data
Body: torrent file + optional path parameter
```

#### Remove Torrent
```
GET /gui/?token=TOKEN&action=remove&hash=HASH
```

#### Remove Torrent with Data
```
GET /gui/?token=TOKEN&action=removedata&hash=HASH
```

#### Start Torrent
```
GET /gui/?token=TOKEN&action=start&hash=HASH
```

#### Stop Torrent
```
GET /gui/?token=TOKEN&action=stop&hash=HASH
```

#### Pause Torrent
```
GET /gui/?token=TOKEN&action=pause&hash=HASH
```

#### Force Start (bypass queue)
```
GET /gui/?token=TOKEN&action=forcestart&hash=HASH
```

#### Recheck Torrent
```
GET /gui/?token=TOKEN&action=recheck&hash=HASH
```

#### Get Torrent Properties
```
GET /gui/?token=TOKEN&action=getprops&hash=HASH
```

#### Set Torrent Properties
```
GET /gui/?token=TOKEN&action=setprops&hash=HASH&s=SETTING&v=VALUE

Settings:
- seed_override: 1/0
- seed_ratio: ratio * 1000
- seed_time: seconds
- ulslots: upload slots
- max_download: download limit (bytes/s)
- max_upload: upload limit (bytes/s)
```

#### Get Files
```
GET /gui/?token=TOKEN&action=getfiles&hash=HASH

Response format:
{
  "files": [HASH, [
    [filename, filesize, downloaded, priority, first_piece, num_pieces, streamable, encoded_rate, duration, width, height, stream_eta, streamability],
    ...
  ]]
}

Priority values:
0 = Don't download
1 = Low
2 = Normal
3 = High
```

#### Set File Priority
```
GET /gui/?token=TOKEN&action=setprio&hash=HASH&p=PRIORITY&f=FILE_INDEX
```

#### Get Settings
```
GET /gui/?token=TOKEN&action=getsettings
```

#### Set Setting
```
GET /gui/?token=TOKEN&action=setsetting&s=SETTING_NAME&v=VALUE
```

## Migration Checklist

### Core Implementation (Completed ✓)
- [x] **Implement TokenInterceptor** - Completed with JSoup HTML parsing
- [x] **Update OkHttpTransportManager for GET requests** - Switched from POST to GET, added query parameter support
- [x] **Create base Request class with query parameter support** - Added `getQueryParameters()` method to Request.java
- [x] **Convert TorrentGetRequest** - Maps to `?list=1`
- [x] **Convert AddTorrentByUrlRequest** - Maps to `?action=add-url&s=URL`
- [x] **Convert AddTorrentByFileRequest** - Maps to `?action=add-file` (multipart upload)
- [x] **Convert TorrentRemoveRequest** - Maps to `?action=remove&hash=HASH` or `?action=removedata`
- [x] **Convert StartTorrentRequest** - Via TorrentActionRequest, maps to `?action=start&hash=HASH`
- [x] **Convert StopTorrentRequest** - Via TorrentActionRequest, maps to `?action=stop&hash=HASH`
- [x] **Convert VerifyTorrentRequest** - Via TorrentActionRequest, maps to `?action=recheck&hash=HASH`
- [x] **Convert SessionGetRequest** - Maps to `?action=getsettings`
- [x] **Convert SessionSetRequest** - Maps to `?action=setsetting` (simplified implementation, needs full field iteration)
- [x] **Implement response array parsing** - Created UTorrentResponseParser with full array-to-object mapping
- [x] **Implement status code interpretation** - Created `mapUTorrentStatus()` to convert bitfield to Transmission enum
- [x] **Update Torrent model** - Added hash field, Builder pattern for all fields, and getter methods

### Pending Tasks
- [ ] **Convert TorrentSetRequest** - Implement full property setting support (currently basic)
- [ ] **Complete SessionSetRequest** - Iterate arguments and add each as s/v parameter pairs
- [ ] **Update mock server** - Simulate uTorrent API responses for testing
- [ ] **Write unit tests** - Test individual components (parser, status mapping, etc.)
- [ ] **Write integration tests** - Test full request/response cycles
- [ ] **Write E2E tests** - Test with real uTorrent instance
- [ ] **Support multiple torrents** - Implement repeated `&hash=` parameters in URL builder
- [ ] **Add file operations** - Complete implementation of file priority and getfiles actions

### Build Status
✅ **Build Successful** - All code compiles without errors (as of 2025-10-10)

## Notes

- uTorrent uses hash strings instead of numeric IDs
- Hashes are 40-character hex strings (SHA-1)
- Multiple torrents can be operated on by repeating `&hash=` parameter
- Progress is in per mille (0-1000) not percentage
- Ratio is multiplied by 1000
- All speeds are in bytes per second
- ETA is in seconds (-1 for unknown/infinite)
