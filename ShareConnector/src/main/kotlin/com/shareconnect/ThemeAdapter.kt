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
import com.shareconnect.database.Theme

class ThemeAdapter(private val listener: OnThemeSelectListener) :
    RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {
    private val themes: MutableList<Theme> = ArrayList()

    interface OnThemeSelectListener {
        fun onThemeSelected(theme: Theme)
        fun onThemeEdit(theme: Theme)
    }

    fun updateThemes(themes: List<Theme>) {
        this.themes.clear()
        this.themes.addAll(themes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_theme, parent, false)
        return ThemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themes[position]
        holder.bind(theme)
    }

    override fun getItemCount(): Int {
        return themes.size
    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewThemeName: TextView = itemView.findViewById(R.id.textViewThemeName)
        private val textViewThemeVariant: TextView = itemView.findViewById(R.id.textViewThemeVariant)
        private val textViewThemeType: TextView = itemView.findViewById(R.id.textViewThemeType)
        private val buttonSelectTheme: MaterialButton = itemView.findViewById(R.id.buttonSelectTheme)
        private val buttonEditTheme: MaterialButton = itemView.findViewById(R.id.buttonEditTheme)

        fun bind(theme: Theme) {
            textViewThemeName.text = theme.name
            textViewThemeVariant.text = if (theme.isDarkMode) {
                itemView.context.getString(R.string.dark)
            } else {
                itemView.context.getString(R.string.light)
            }

            // Show theme type indicator for custom themes
            if (theme.isCustom) {
                textViewThemeType.text = "CUSTOM"
                textViewThemeType.visibility = View.VISIBLE
                buttonEditTheme.visibility = View.VISIBLE
            } else {
                textViewThemeType.visibility = View.GONE
                buttonEditTheme.visibility = View.GONE
            }

            buttonSelectTheme.setOnClickListener {
                listener.onThemeSelected(theme)
            }

            buttonEditTheme.setOnClickListener {
                listener.onThemeEdit(theme)
            }
        }
    }
}