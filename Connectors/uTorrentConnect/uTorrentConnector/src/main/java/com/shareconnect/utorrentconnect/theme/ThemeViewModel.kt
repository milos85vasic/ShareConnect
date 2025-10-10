package com.shareconnect.utorrentconnect.theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.shareconnect.utorrentconnect.logging.Logger
import com.shareconnect.utorrentconnect.preferences.PreferencesRepository

class ThemeViewModel constructor(
    private val preferencesRepository: PreferencesRepository,
    private val logger: Logger
) : ViewModel() {

    val nightMode = MutableLiveData<NightMode>()

    init {
        viewModelScope.launch {
            preferencesRepository.getNightMode()
                .catch { error ->
                    logger.log(error)
                }
                .collect { mode ->
                    nightMode.value = mode
                }
        }
    }

    fun onNightModeSelected(mode: NightMode) {
        viewModelScope.launch {
            preferencesRepository.setNightMode(mode)
        }
    }
}
