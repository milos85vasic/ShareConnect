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


package com.shareconnect.utorrentconnect.transport.okhttp

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.octo.android.robospice.persistence.exception.SpiceException
import com.octo.android.robospice.request.listener.RequestListener
import com.shareconnect.utorrentconnect.server.Server
import com.shareconnect.utorrentconnect.transport.TransportManager
import com.shareconnect.utorrentconnect.transport.request.Request
import com.shareconnect.utorrentconnect.transport.response.UTorrentResponseParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * OkHttp-based transport manager for uTorrent Web API.
 *
 * Key differences from Transmission protocol:
 * - Uses GET requests instead of POST
 * - Uses query parameters instead of JSON body
 * - Uses token authentication instead of session ID
 * - Response format varies (sometimes JSON object, sometimes array-based)
 */
class OkHttpTransportManager(
    private val server: Server
) : TransportManager {

    private val okHttpClient = OkHttpClient.Builder().apply {

        // uTorrent uses token-based authentication
        addInterceptor(TokenInterceptor(server))
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))

        if (server.isAuthenticationEnabled) {
            authenticator(BasicAuthenticator(server.userName.orEmpty(), server.password.orEmpty()))
        }
        if (server.trustSelfSignedSslCert) {
            // Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager")
            val trustAllManager = object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustAllManager), SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory(sslContext.socketFactory, trustAllManager)
            hostnameVerifier { _, _ -> true }
        }
    }.build()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun <T : Any?> doRequest(request: Request<T>, listener: RequestListener<T>?) {
        request.server = server

        // Build URL with query parameters for uTorrent Web API
        val url = try {
            HttpUrl.Builder().apply {
                scheme(if (server.useHttps()) "https" else "http")
                port(server.port)
                host(server.host.trim('/', '\\'))

                // uTorrent Web API uses /gui/ endpoint
                addPathSegments("gui/")

                // Add query parameters from request
                request.queryParameters.forEach { (key, value) ->
                    addQueryParameter(key, value)
                }
            }.build()
        } catch (e: Throwable) {
            notifyListenerFailure(
                error = SpiceException("Invalid host, port or RPC path", e),
                listener = listener
            )
            return
        }

        // uTorrent uses GET requests with query parameters (token added by interceptor)
        val okHttpRequest = okhttp3.Request.Builder()
            .get()  // Changed from POST to GET
            .url(url)
            .build()

        okHttpClient.newCall(okHttpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    request.setResponse(response.code, responseBody)

                    if (response.code in 200..299) {
                        if (listener != null && responseBody != null) {
                            try {
                                // Parse result based on request type
                                val result = parseUTorrentResponse(responseBody, request)
                                notifyListenerSuccess(result, listener)
                            } catch (e: Exception) {
                                // If parsing fails, try returning raw response
                                notifyListenerFailure(
                                    error = SpiceException("Failed to parse response", e),
                                    listener = listener
                                )
                            }
                        }
                    } else {
                        val error = SpiceException("Request failed with ${response.code} HTTP status code")
                        request.error = error
                        notifyListenerFailure(
                            error = error,
                            listener = listener
                        )
                    }
                } catch (e: Throwable) {
                    request.error = e
                    notifyListenerFailure(
                        error = SpiceException("Failed to execute request", e),
                        listener = listener
                    )
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                val error = SpiceException(e.message, e)
                request.error = error
                notifyListenerFailure(
                    error = error,
                    listener = listener
                )
            }
        })
    }

    /**
     * Parse uTorrent Web API response.
     *
     * uTorrent responses vary by endpoint:
     * - List torrents: JSON with "torrents" array (array-based format)
     * - Actions: JSON with status or empty
     * - Settings: JSON object
     *
     * This method parses uTorrent's array-based format and wraps it in
     * a Transmission-compatible structure for seamless integration.
     */
    private fun <T> parseUTorrentResponse(responseBody: String, request: Request<T>): T {
        val responseJson = JSONObject(responseBody)

        // For list requests, use custom parser to handle array format
        if (responseJson.has("torrents")) {
            // Parse uTorrent's array-based format and wrap in Transmission format
            val wrappedResponse = UTorrentResponseParser.wrapInTransmissionFormat(responseBody)

            val wrappedJson = JSONObject(wrappedResponse)
            return JSON_PARSER.parseAndClose(
                StringReader(wrappedJson.getString("arguments")),
                request.resultType
            )
        }

        // For action requests (no response body expected), create empty result
        if (responseBody.isEmpty() || responseBody == "{}") {
            // Return null for Void types
            @Suppress("UNCHECKED_CAST")
            return null as T
        }

        // For other requests, return the response as-is
        return JSON_PARSER.parseAndClose(
            StringReader(responseBody),
            request.resultType
        )
    }

    private fun <T : Any?> notifyListenerSuccess(result: T, listener: RequestListener<T>?) {
        listener ?: return
        mainThreadHandler.post {
            listener.onRequestSuccess(result)
        }
    }

    private fun <T : Any?> notifyListenerFailure(
        error: SpiceException,
        listener: RequestListener<T>?
    ) {
        listener ?: return
        mainThreadHandler.post {
            listener.onRequestFailure(error)
        }
    }

    override fun <T : Any?> doRequest(
        request: Request<T>,
        listener: RequestListener<T>?,
        delay: Long
    ) {
        Handler(Looper.getMainLooper()).postDelayed(
            { doRequest(request, listener) },
            delay
        )
    }

    override fun isStarted(): Boolean {
        return true
    }

    companion object {
        private val JSON_PARSER = JsonObjectParser.Builder(JacksonFactory.getDefaultInstance()).build()
    }
}
