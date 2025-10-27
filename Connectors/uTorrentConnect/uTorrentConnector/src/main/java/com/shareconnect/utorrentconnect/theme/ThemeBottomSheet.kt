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


package com.shareconnect.utorrentconnect.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.uTorrentRemote
import com.shareconnect.utorrentconnect.databinding.ThemeBottomsheetBinding

class ThemeBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ThemeViewModel by activityViewModels {
        val app = uTorrentRemote.getApplication(requireContext())
        ThemeViewModelFactory(app.preferencesRepository, app.logger)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ThemeBottomsheetBinding.inflate(
            inflater.cloneInContext(ContextThemeWrapper(activity, R.style.AppTheme)),
            container,
            false
        )
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radio_off -> NightMode.OFF
                R.id.radio_on -> NightMode.ON
                else -> NightMode.AUTO
            }
            viewModel.onNightModeSelected(mode)
        }

        viewModel.nightMode.observe(viewLifecycleOwner) { nightMode ->
            nightMode ?: return@observe
            when (nightMode) {
                NightMode.OFF -> binding.radioOff.isChecked = true
                NightMode.ON -> binding.radioOn.isChecked = true
                NightMode.AUTO -> binding.radioDeviceSettings.isChecked = true
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "ThemeBottomSheet"
        fun newInstance(): ThemeBottomSheet = ThemeBottomSheet()
    }
}
