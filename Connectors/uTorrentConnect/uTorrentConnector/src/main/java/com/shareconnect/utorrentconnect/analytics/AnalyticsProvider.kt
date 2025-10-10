package com.shareconnect.utorrentconnect.analytics

interface AnalyticsProvider {
    fun logEvent(name: String, vararg params: Pair<String, Any?>)
    fun setUserProperty(name: String, value: String?)
}
