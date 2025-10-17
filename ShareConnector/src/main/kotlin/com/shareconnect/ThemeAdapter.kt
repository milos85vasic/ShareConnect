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