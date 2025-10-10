package com.shareconnect.utorrentconnect.transport.okhttp

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly

/**
 * Interceptor for uTorrent Web API token-based authentication.
 *
 * uTorrent requires a token for each API request to prevent CSRF attacks.
 * The token is fetched from /gui/token.html and added as a query parameter to all requests.
 *
 * Token lifecycle:
 * 1. First request: Fetch token from /gui/token.html
 * 2. Subsequent requests: Add token as query parameter
 * 3. On 401 response: Token expired, fetch new token and retry
 *
 * See: API_IMPLEMENTATION.md for full details
 */
class TokenInterceptor(
    private val server: com.shareconnect.utorrentconnect.server.Server
) : Interceptor {

    private var token: String? = null

    companion object {
        private const val TAG = "TokenInterceptor"
        private const val TOKEN_PARAM = "token"
        private const val TOKEN_ENDPOINT = "/gui/token.html"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Ensure we have a valid token
        if (token == null) {
            token = fetchToken(chain)
        }

        // Add token to request if not already present and not the token fetch request
        if (!request.url.encodedPath.contains(TOKEN_ENDPOINT)) {
            request = request.addTokenParameter()
        }

        var response = chain.proceed(request)

        // Handle 401 (token expired/invalid) - fetch new token and retry
        if (response.code == 401 && !request.url.encodedPath.contains(TOKEN_ENDPOINT)) {
            Log.w(TAG, "Token expired or invalid (401), fetching new token")
            response.closeQuietly()

            token = fetchToken(chain)
            request = chain.request().addTokenParameter()
            response = chain.proceed(request)
        }

        return response
    }

    /**
     * Fetch token from uTorrent WebUI.
     *
     * Endpoint: GET /gui/token.html
     * Response format: <html><div id='token'>TOKEN_VALUE</div></html>
     *
     * @return The extracted token string, or null if fetch failed
     */
    private fun fetchToken(chain: Interceptor.Chain): String? {
        try {
            val tokenUrl = buildTokenUrl()
            val tokenRequest = Request.Builder()
                .get()
                .url(tokenUrl)
                .build()

            val tokenResponse = chain.proceed(tokenRequest)

            if (tokenResponse.isSuccessful) {
                val html = tokenResponse.body?.string()
                tokenResponse.closeQuietly()

                if (html != null) {
                    // Parse HTML and extract token from <div id='token'>
                    // TODO: Implement HTML parsing when JSoup dependency is added
                    // For now, use simple string extraction
                    val extractedToken = extractTokenFromHtml(html)

                    if (extractedToken != null) {
                        Log.d(TAG, "Successfully fetched token")
                        return extractedToken
                    } else {
                        Log.e(TAG, "Token element not found in HTML response")
                    }
                } else {
                    Log.e(TAG, "Empty response body when fetching token")
                }
            } else {
                Log.e(TAG, "Failed to fetch token: HTTP ${tokenResponse.code}")
                tokenResponse.closeQuietly()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching token", e)
        }

        return null
    }

    /**
     * Extract token from HTML response using JSoup.
     * Format: <html><div id='token'>TOKEN_VALUE</div></html>
     */
    private fun extractTokenFromHtml(html: String): String? {
        return try {
            val document = org.jsoup.Jsoup.parse(html)
            val tokenElement = document.select("div#token").first()
            tokenElement?.text()?.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing token from HTML", e)
            null
        }
    }

    /**
     * Build the full URL for token endpoint
     */
    private fun buildTokenUrl(): String {
        val protocol = if (server.useHttps()) "https" else "http"
        return "$protocol://${server.host}:${server.port}$TOKEN_ENDPOINT"
    }

    /**
     * Add token as query parameter to the request
     */
    private fun Request.addTokenParameter(): Request {
        val currentToken = token ?: return this

        val newUrl = url.newBuilder()
            .addQueryParameter(TOKEN_PARAM, currentToken)
            .build()

        return newBuilder()
            .url(newUrl)
            .build()
    }
}

/**
 * This interceptor is ready for use with uTorrent Web API.
 * JSoup dependency has been added for proper HTML parsing.
 *
 * Note: This interceptor is currently NOT active in OkHttpTransportManager.
 * It will be activated as part of the full API migration.
 *
 * See API_IMPLEMENTATION.md for comprehensive migration guide.
 */
