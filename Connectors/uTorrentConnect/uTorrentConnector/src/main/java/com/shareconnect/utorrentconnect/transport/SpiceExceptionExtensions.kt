package com.shareconnect.utorrentconnect.transport

import com.octo.android.robospice.persistence.exception.SpiceException
import com.shareconnect.utorrentconnect.transport.request.ResponseFailureException

val SpiceException.responseFailureMessage: String?
    get() = (this.cause as? ResponseFailureException)?.failureMessage
