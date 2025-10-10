package com.shareconnect.utorrentconnect.transport

import android.os.Bundle
import com.shareconnect.utorrentconnect.BaseActivity
import com.shareconnect.utorrentconnect.uTorrentRemote
import com.shareconnect.utorrentconnect.uTorrentRemote.OnActiveServerChangedListener
import com.shareconnect.utorrentconnect.server.Server
import com.shareconnect.utorrentconnect.transport.okhttp.OkHttpTransportManager

abstract class BaseSpiceActivity : BaseActivity() {

    private var transportManager: TransportManager? = null
    private val activeServerListener = OnActiveServerChangedListener { newServer: Server? ->
        transportManager = updatedTransportManager(newServer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as uTorrentRemote
        app.addOnActiveServerChangedListener(activeServerListener)
        transportManager = updatedTransportManager(app.getActiveServer())
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        (transportManager as? SpiceTransportManager)?.start(this)
        super.onStart()
    }

    override fun onStop() {
        (transportManager as? SpiceTransportManager)?.apply {
            if (isStarted) {
                cancelAllRequests()
                shouldStop()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        val app = application as uTorrentRemote
        app.removeOnActiveServerChangedListener(activeServerListener)
        super.onDestroy()
    }

    fun getTransportManager(): TransportManager {
        return checkNotNull(transportManager) { "Transport manager is not initialized yet" }
    }

    private fun updatedTransportManager(server: Server?): TransportManager? {
        val app = uTorrentRemote.instance
        return if (app.featureManager.useOkHttp()) {
            server?.let { OkHttpTransportManager(server) }
        } else {
            ((transportManager as? SpiceTransportManager) ?: SpiceTransportManager()).apply {
                setServer(server)
            }
        }
    }
}
