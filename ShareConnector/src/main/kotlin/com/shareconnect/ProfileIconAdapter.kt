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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileIconAdapter(
    private val context: Context,
    private val profiles: List<ServerProfile>,
    private val onProfileClick: (ServerProfile) -> Unit,
    private val onProfileLongClick: (ServerProfile) -> Boolean
) : RecyclerView.Adapter<ProfileIconAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_profile_icon, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile)
    }

    override fun getItemCount(): Int = profiles.size

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileIcon: ImageView = itemView.findViewById(R.id.profileIcon)
        private val profileName: TextView = itemView.findViewById(R.id.profileName)
        private val profileType: TextView = itemView.findViewById(R.id.profileType)
        private val defaultIndicator: ImageView = itemView.findViewById(R.id.defaultIndicator)
        private val lockIndicator: ImageView = itemView.findViewById(R.id.lockIndicator)

        fun bind(profile: ServerProfile) {
            profileName.text = profile.name ?: "Unnamed"
            profileType.text = profile.getServiceTypeName(context)

            // Set appropriate icon based on service type
            val iconRes = when (profile.serviceType) {
                ServerProfile.TYPE_METUBE -> R.drawable.ic_service_metube
                ServerProfile.TYPE_YTDL -> R.drawable.ic_service_metube // YT-DLP uses same icon as MeTube for now
                ServerProfile.TYPE_TORRENT -> R.drawable.ic_service_torrent
                ServerProfile.TYPE_JDOWNLOADER -> R.drawable.ic_service_jdownloader
                else -> R.drawable.ic_service_metube // Default to MeTube icon
            }
            profileIcon.setImageResource(iconRes)

            // Show default indicator if this is the default profile
            defaultIndicator.visibility = if (profile.isDefault) View.VISIBLE else View.GONE

            // Show lock indicator if profile has credentials
            lockIndicator.visibility = if (!profile.username.isNullOrEmpty() && !profile.password.isNullOrEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Handle clicks
            itemView.setOnClickListener {
                onProfileClick(profile)
            }

            itemView.setOnLongClickListener {
                onProfileLongClick(profile)
            }
        }
    }
}