package com.shareconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

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
            .inflate(R.layout.item_language, parent, false)
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
        private val buttonSelectLanguage: MaterialButton = itemView.findViewById(R.id.buttonSelectLanguage)

        fun bind(language: Pair<String, String>, isSelected: Boolean) {
            val (code, displayName) = language

            textViewLanguageName.text = displayName
            textViewLanguageCode.text = code

            if (isSelected) {
                buttonSelectLanguage.text = itemView.context.getString(R.string.selected)
                buttonSelectLanguage.isEnabled = false
            } else {
                buttonSelectLanguage.text = itemView.context.getString(R.string.select)
                buttonSelectLanguage.isEnabled = true
            }

            buttonSelectLanguage.setOnClickListener {
                listener.onLanguageSelected(code, displayName)
            }
        }
    }
}
