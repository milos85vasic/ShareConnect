package com.shareconnect.wireguardconnect.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

/**
 * VPN service for WireGuard tunnels.
 * This is a placeholder implementation - a full implementation would require
 * native WireGuard integration or use of the WireGuard Android library.
 */
class WireGuardVpnService : VpnService() {

    companion object {
        private const val TAG = "WireGuardVpnService"
    }

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "VPN service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "VPN service destroyed")
        stopVpn()
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping VPN", e)
        }
    }
}
