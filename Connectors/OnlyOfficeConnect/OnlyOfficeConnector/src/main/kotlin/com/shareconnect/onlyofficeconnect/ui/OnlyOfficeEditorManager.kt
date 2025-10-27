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


package com.shareconnect.onlyofficeconnect.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import com.shareconnect.onlyofficeconnect.data.models.*

/**
 * Manager for ONLYOFFICE Document Editor WebView integration
 * Handles document loading, editing, and collaboration features
 */
class OnlyOfficeEditorManager(
    private val context: Context,
    private val webView: WebView,
    private val callbacks: EditorCallbacks
) {

    private val tag = "OnlyOfficeEditorManager"
    private val gson = Gson()

    interface EditorCallbacks {
        fun onDocumentReady()
        fun onDocumentStateChange(isSaved: Boolean)
        fun onError(error: String)
        fun onInfo(info: String)
        fun onWarning(warning: String)
        fun onRequestSaveAs(saveAsEvent: SaveAsEvent)
        fun onRequestInsertImage(insertImageEvent: InsertImageEvent)
        fun onRequestMailMergeRecipients()
        fun onRequestCompareFile()
        fun onRequestRestore(version: Int)
        fun onMetaChange(metaData: Map<String, Any>)
        fun onRequestClose()
    }

    data class SaveAsEvent(
        val title: String,
        val url: String,
        val fileType: String
    )

    data class InsertImageEvent(
        val c: String, // callback name
        val token: String? = null
    )

    init {
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // Enable WebView debugging in debug builds
                WebView.setWebContentsDebuggingEnabled(true)
            }

            webViewClient = OnlyOfficeWebViewClient()
            webChromeClient = OnlyOfficeWebChromeClient()

            // Add JavaScript interface for callbacks
            addJavascriptInterface(OnlyOfficeJSInterface(), "AndroidInterface")
        }
    }

    /**
     * Load ONLYOFFICE Document Editor with configuration
     */
    fun loadEditor(
        documentServerUrl: String,
        editorConfig: OnlyOfficeEditorConfiguration
    ) {
        try {
            val configJson = gson.toJson(editorConfig)
            val html = generateEditorHtml(documentServerUrl, configJson)

            Log.d(tag, "Loading editor with config: $configJson")
            webView.loadDataWithBaseURL(
                documentServerUrl,
                html,
                "text/html",
                "UTF-8",
                null
            )
        } catch (e: Exception) {
            Log.e(tag, "Error loading editor", e)
            callbacks.onError("Failed to load editor: ${e.message}")
        }
    }

    /**
     * Generate HTML for ONLYOFFICE Document Editor
     */
    private fun generateEditorHtml(serverUrl: String, configJson: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <title>ONLYOFFICE Document Editor</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    html, body {
                        width: 100%;
                        height: 100%;
                        overflow: hidden;
                    }
                    #placeholder {
                        width: 100%;
                        height: 100%;
                    }
                </style>
                <script type="text/javascript" src="${serverUrl}/web-apps/apps/api/documents/api.js"></script>
            </head>
            <body>
                <div id="placeholder"></div>
                <script type="text/javascript">
                    var config = $configJson;

                    // Add event handlers
                    config.events = {
                        'onAppReady': function() {
                            console.log('Document editor ready');
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onDocumentReady();
                            }
                        },
                        'onDocumentStateChange': function(event) {
                            console.log('Document state changed:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onDocumentStateChange(JSON.stringify(event));
                            }
                        },
                        'onError': function(event) {
                            console.error('Editor error:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onError(JSON.stringify(event));
                            }
                        },
                        'onInfo': function(event) {
                            console.log('Editor info:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onInfo(JSON.stringify(event));
                            }
                        },
                        'onWarning': function(event) {
                            console.warn('Editor warning:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onWarning(JSON.stringify(event));
                            }
                        },
                        'onRequestSaveAs': function(event) {
                            console.log('Save as requested:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestSaveAs(JSON.stringify(event));
                            }
                        },
                        'onRequestInsertImage': function(event) {
                            console.log('Insert image requested:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestInsertImage(JSON.stringify(event));
                            }
                        },
                        'onRequestMailMergeRecipients': function(event) {
                            console.log('Mail merge recipients requested');
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestMailMergeRecipients();
                            }
                        },
                        'onRequestCompareFile': function() {
                            console.log('Compare file requested');
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestCompareFile();
                            }
                        },
                        'onRequestRestore': function(event) {
                            console.log('Restore requested:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestRestore(JSON.stringify(event));
                            }
                        },
                        'onMetaChange': function(event) {
                            console.log('Meta data changed:', event);
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onMetaChange(JSON.stringify(event));
                            }
                        },
                        'onRequestClose': function() {
                            console.log('Close requested');
                            if (window.AndroidInterface) {
                                window.AndroidInterface.onRequestClose();
                            }
                        }
                    };

                    // Initialize the editor
                    var docEditor = new DocsAPI.DocEditor("placeholder", config);

                    // Store reference for later use
                    window.docEditor = docEditor;
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * JavaScript interface for communication between WebView and Android
     */
    inner class OnlyOfficeJSInterface {

        @JavascriptInterface
        fun onDocumentReady() {
            Log.d(tag, "Document ready")
            callbacks.onDocumentReady()
        }

        @JavascriptInterface
        fun onDocumentStateChange(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val isSaved = event["data"] as? Boolean ?: true
                Log.d(tag, "Document state changed: isSaved=$isSaved")
                callbacks.onDocumentStateChange(isSaved)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing document state change event", e)
            }
        }

        @JavascriptInterface
        fun onError(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val errorCode = event["data"] as? String ?: "Unknown error"
                Log.e(tag, "Editor error: $errorCode")
                callbacks.onError(errorCode)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing error event", e)
                callbacks.onError("Unknown error occurred")
            }
        }

        @JavascriptInterface
        fun onInfo(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val info = event["data"]?.toString() ?: ""
                Log.d(tag, "Editor info: $info")
                callbacks.onInfo(info)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing info event", e)
            }
        }

        @JavascriptInterface
        fun onWarning(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val warning = event["data"]?.toString() ?: ""
                Log.w(tag, "Editor warning: $warning")
                callbacks.onWarning(warning)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing warning event", e)
            }
        }

        @JavascriptInterface
        fun onRequestSaveAs(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val data = event["data"] as? Map<*, *>
                val saveAsEvent = SaveAsEvent(
                    title = data?.get("title") as? String ?: "",
                    url = data?.get("url") as? String ?: "",
                    fileType = data?.get("fileType") as? String ?: ""
                )
                Log.d(tag, "Save as requested: ${saveAsEvent.title}")
                callbacks.onRequestSaveAs(saveAsEvent)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing save as event", e)
            }
        }

        @JavascriptInterface
        fun onRequestInsertImage(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val data = event["data"] as? Map<*, *>
                val insertImageEvent = InsertImageEvent(
                    c = data?.get("c") as? String ?: "",
                    token = data?.get("token") as? String
                )
                Log.d(tag, "Insert image requested")
                callbacks.onRequestInsertImage(insertImageEvent)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing insert image event", e)
            }
        }

        @JavascriptInterface
        fun onRequestMailMergeRecipients() {
            Log.d(tag, "Mail merge recipients requested")
            callbacks.onRequestMailMergeRecipients()
        }

        @JavascriptInterface
        fun onRequestCompareFile() {
            Log.d(tag, "Compare file requested")
            callbacks.onRequestCompareFile()
        }

        @JavascriptInterface
        fun onRequestRestore(eventJson: String) {
            try {
                val event = gson.fromJson(eventJson, Map::class.java)
                val data = event["data"] as? Map<*, *>
                val version = (data?.get("version") as? Double)?.toInt() ?: 0
                Log.d(tag, "Restore requested: version=$version")
                callbacks.onRequestRestore(version)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing restore event", e)
            }
        }

        @JavascriptInterface
        fun onMetaChange(eventJson: String) {
            try {
                @Suppress("UNCHECKED_CAST")
                val event = gson.fromJson(eventJson, Map::class.java) as Map<String, Any>
                Log.d(tag, "Meta data changed")
                callbacks.onMetaChange(event)
            } catch (e: Exception) {
                Log.e(tag, "Error parsing meta change event", e)
            }
        }

        @JavascriptInterface
        fun onRequestClose() {
            Log.d(tag, "Close requested")
            callbacks.onRequestClose()
        }
    }

    /**
     * Custom WebViewClient for handling page loading
     */
    private inner class OnlyOfficeWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d(tag, "Page loaded: $url")
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            Log.e(tag, "WebView error: $description")
            callbacks.onError("Failed to load document: $description")
        }
    }

    /**
     * Custom WebChromeClient for handling JavaScript
     */
    private inner class OnlyOfficeWebChromeClient : WebChromeClient() {
        override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
            Log.d(tag, "Console: ${message.message()} (${message.sourceId()}:${message.lineNumber()})")
            return true
        }
    }

    /**
     * Execute JavaScript in the editor
     */
    fun executeScript(script: String) {
        webView.post {
            webView.evaluateJavascript(script) { result ->
                Log.d(tag, "Script result: $result")
            }
        }
    }

    /**
     * Destroy the editor and clean up resources
     */
    fun destroy() {
        try {
            webView.loadUrl("about:blank")
            webView.removeAllViews()
            webView.destroy()
        } catch (e: Exception) {
            Log.e(tag, "Error destroying WebView", e)
        }
    }
}
