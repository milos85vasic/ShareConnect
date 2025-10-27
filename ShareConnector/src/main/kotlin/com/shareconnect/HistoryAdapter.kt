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


package com.shareconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.shareconnect.historysync.models.HistoryData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val listener: OnHistoryItemClickListener) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val historyItems: MutableList<HistoryData> = ArrayList()

    interface OnHistoryItemClickListener {
        fun onResendClick(item: HistoryData)
        fun onDeleteClick(item: HistoryData)
    }

    fun updateHistoryItems(items: List<HistoryData>) {
        historyItems.clear()
        historyItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return historyItems.size
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewUrl: TextView = itemView.findViewById(R.id.textViewUrl)
        private val textViewServiceProvider: TextView = itemView.findViewById(R.id.textViewServiceProvider)
        private val textViewType: TextView = itemView.findViewById(R.id.textViewType)
        private val textViewProfile: TextView = itemView.findViewById(R.id.textViewProfile)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
        private val textViewStatus: TextView = itemView.findViewById(R.id.textViewStatus)
        private val textViewServiceType: TextView = itemView.findViewById(R.id.textViewServiceType)
        private val textViewDuration: TextView = itemView.findViewById(R.id.textViewDuration)
        private val textViewQuality: TextView = itemView.findViewById(R.id.textViewQuality)
        private val textViewFileSize: TextView = itemView.findViewById(R.id.textViewFileSize)
        private val textViewTorrentClient: TextView = itemView.findViewById(R.id.textViewTorrentClient)
        private val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)
        private val textViewTags: TextView = itemView.findViewById(R.id.textViewTags)
        private val textViewSourceApp: TextView = itemView.findViewById(R.id.textViewSourceApp)
        private val layoutMediaDetails: ViewGroup = itemView.findViewById(R.id.layoutMediaDetails)
        private val layoutTorrentDetails: ViewGroup = itemView.findViewById(R.id.layoutTorrentDetails)
        private val buttonResend: MaterialButton = itemView.findViewById(R.id.buttonResend)
        private val buttonDelete: MaterialButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(item: HistoryData) {
            // Basic information
            textViewTitle.text = item.getDisplayTitle()
            textViewDescription.text = item.description ?: ""
            textViewUrl.text = item.url
            textViewServiceProvider.text = item.serviceProvider ?: "Unknown"
            textViewType.text = item.type

            // Service type with fallback
            val serviceTypeText = when (item.serviceType) {
                HistoryData.SERVICE_METUBE -> itemView.context.getString(R.string.metube)
                HistoryData.SERVICE_YTDL -> "YTDL"
                HistoryData.SERVICE_TORRENT -> "Torrent"
                HistoryData.SERVICE_JDOWNLOADER -> "JDownloader"
                else -> item.serviceType ?: itemView.context.getString(R.string.metube)
            }
            textViewServiceType.text = serviceTypeText

            // Profile information
            val profileName = item.profileName
            textViewProfile.text = if (profileName != null && profileName.isNotEmpty()) {
                "Profile: $profileName"
            } else {
                itemView.context.getString(R.string.not_sent)
            }

            // Source app
            textViewSourceApp.text = "from ${item.sourceApp}"

            // Format timestamp
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            textViewTimestamp.text = sdf.format(Date(item.timestamp))

            // Set status
            if (item.profileId.isNullOrEmpty()) {
                textViewStatus.setText(R.string.not_sent)
                textViewStatus.setBackgroundResource(R.drawable.status_background)
            } else {
                textViewStatus.text = if (item.isSentSuccessfully) "Sent" else "Failed"
                if (item.isSentSuccessfully) {
                    textViewStatus.setBackgroundResource(R.drawable.status_background)
                } else {
                    textViewStatus.setBackgroundResource(R.drawable.tag_background)
                }
            }

            // Show/hide description
            textViewDescription.visibility = if (item.description.isNullOrEmpty()) View.GONE else View.VISIBLE

            // Media details (duration, quality, file size)
            val hasMediaDetails = item.duration != null || item.quality != null || item.fileSize != null
            layoutMediaDetails.visibility = if (hasMediaDetails) View.VISIBLE else View.GONE

            if (hasMediaDetails) {
                textViewDuration.text = item.getFormattedDuration() ?: ""
                textViewDuration.visibility = if (item.duration != null) View.VISIBLE else View.GONE

                textViewQuality.text = item.quality ?: ""
                textViewQuality.visibility = if (item.quality != null) View.VISIBLE else View.GONE

                textViewFileSize.text = item.getFormattedFileSize() ?: ""
                textViewFileSize.visibility = if (item.fileSize != null) View.VISIBLE else View.GONE
            }

            // Torrent details (client, category, tags)
            val hasTorrentDetails = item.isTorrentHistory() || item.category != null || item.tags != null
            layoutTorrentDetails.visibility = if (hasTorrentDetails) View.VISIBLE else View.GONE

            if (hasTorrentDetails) {
                val torrentClientText = when (item.torrentClientType) {
                    HistoryData.TORRENT_CLIENT_QBITTORRENT -> "qBittorrent"
                    HistoryData.TORRENT_CLIENT_TRANSMISSION -> "Transmission"
                    HistoryData.TORRENT_CLIENT_UTORRENT -> "µTorrent"
                    else -> item.torrentClientType ?: "Torrent"
                }
                textViewTorrentClient.text = torrentClientText
                textViewTorrentClient.visibility = if (item.isTorrentHistory()) View.VISIBLE else View.GONE

                textViewCategory.text = item.category ?: ""
                textViewCategory.visibility = if (item.category != null) View.VISIBLE else View.GONE

                val tagsText = item.getTagsList().take(3).joinToString(", ") // Limit to 3 tags
                textViewTags.text = tagsText
                textViewTags.visibility = if (tagsText.isNotEmpty()) View.VISIBLE else View.GONE
            }

            // Set button listeners
            buttonResend.setOnClickListener {
                listener.onResendClick(item)
            }

            buttonDelete.setOnClickListener {
                listener.onDeleteClick(item)
            }
        }
    }
}