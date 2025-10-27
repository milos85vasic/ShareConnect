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


package com.shareconnect.utorrentconnect.preferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shareconnect.utorrentconnect.R

class LanguageAdapter(private val listener: OnLanguageSelectListener) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private val languages: MutableList<Pair<String, String>> = ArrayList()
    private var selectedLanguageCode: String = ""

    interface OnLanguageSelectListener {
        fun onLanguageSelected(languageCode: String, displayName: String)
    }

    fun updateLanguages(languages: List<Pair<String, String>>, currentLanguageCode: String) {
        this.languages.clear()
        this.languages.addAll(languages)
        this.selectedLanguageCode = currentLanguageCode
        notifyDataSetChanged()
    }

    fun setSelectedLanguage(languageCode: String) {
        this.selectedLanguageCode = languageCode
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language_transmission, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language, language.first == selectedLanguageCode)
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewLanguageName: TextView = itemView.findViewById(R.id.textViewLanguageName)
        private val textViewLanguageCode: TextView = itemView.findViewById(R.id.textViewLanguageCode)
        private val buttonSelectLanguage: Button = itemView.findViewById(R.id.buttonSelectLanguage)

        fun bind(language: Pair<String, String>, isSelected: Boolean) {
            val (code, displayName) = language

            textViewLanguageName.text = displayName
            textViewLanguageCode.text = code

            if (isSelected) {
                buttonSelectLanguage.text = "Selected"
                buttonSelectLanguage.isEnabled = false
            } else {
                buttonSelectLanguage.text = "Select"
                buttonSelectLanguage.isEnabled = true
            }

            buttonSelectLanguage.setOnClickListener {
                listener.onLanguageSelected(code, displayName)
            }
        }
    }
}
