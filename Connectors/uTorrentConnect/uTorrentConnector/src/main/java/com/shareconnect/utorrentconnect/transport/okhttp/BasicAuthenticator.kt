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

import android.util.Base64
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class BasicAuthenticator(private val login: String, private val password: String): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val authHeaderName = when (response.code) {
            401 -> "Authorization"
            407 -> "Proxy-Authorization"
            else -> throw IllegalStateException("Unknown status code: ${response.code}")
        }
        return requestWithAuthentication(response.request, authHeaderName)
    }

    private fun requestWithAuthentication(request: Request, authHeaderName: String): Request? {
        if (request.header(authHeaderName) != null) return null // Give up, we've already failed to authenticate

        val credentials = Base64.encodeToString("$login:$password".toByteArray(), Base64.NO_WRAP)
        return request.newBuilder()
            .header(authHeaderName, "Basic $credentials")
            .build()
    }
}
