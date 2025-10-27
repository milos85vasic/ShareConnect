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


package com.shareconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shareconnect.R
import com.shareconnect.utils.SystemAppDetector

class SystemAppAdapter(
    private val onAppClick: (SystemAppDetector.AppInfo) -> Unit
) : ListAdapter<SystemAppDetector.AppInfo, SystemAppAdapter.AppViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_system_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position), onAppClick)
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val appPackage: TextView = itemView.findViewById(R.id.appPackage)

        fun bind(appInfo: SystemAppDetector.AppInfo, onAppClick: (SystemAppDetector.AppInfo) -> Unit) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.appName
            appPackage.text = appInfo.packageName

            itemView.setOnClickListener {
                onAppClick(appInfo)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SystemAppDetector.AppInfo>() {
        override fun areItemsTheSame(
            oldItem: SystemAppDetector.AppInfo,
            newItem: SystemAppDetector.AppInfo
        ): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(
            oldItem: SystemAppDetector.AppInfo,
            newItem: SystemAppDetector.AppInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
}