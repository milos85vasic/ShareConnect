package com.shareconnect.utorrentconnect.e2e.robots

import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.e2e.utils.assertViewWithIdDisplayed
import com.shareconnect.utorrentconnect.e2e.utils.clickId
import com.shareconnect.utorrentconnect.e2e.utils.inputText

class AddServerRobot {

    fun enterServerName(name: String) {
        inputText(R.id.server_name_edit_text, name)
    }

    fun enterHostName(name: String) {
        inputText(R.id.host_edit_text, name)
    }

    fun enterPort(port: Int) {
        inputText(R.id.port_edit_text, port.toString())
    }

    fun clickOk() {
        clickId(R.id.ok_button)
    }

    companion object {
        fun addServer(func: AddServerRobot.() -> Unit): AddServerRobot {
            assertViewWithIdDisplayed(R.id.add_server_fragment_container)
            return AddServerRobot().apply(func)
        }
    }
}
