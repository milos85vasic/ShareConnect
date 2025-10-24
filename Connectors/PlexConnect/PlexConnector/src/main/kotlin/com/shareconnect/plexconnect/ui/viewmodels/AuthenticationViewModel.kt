package com.shareconnect.plexconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.plexconnect.service.PlexAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authService: PlexAuthService
) : ViewModel() {

    val authState: StateFlow<PlexAuthService.AuthState> = authService.authState

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    fun startAuthentication() {
        if (authService.isAuthenticating()) {
            return // Already authenticating
        }

        viewModelScope.launch {
            _isAuthenticating.value = true
            authService.startAuthentication()
            _isAuthenticating.value = false
        }
    }

    fun cancelAuthentication() {
        authService.cancelAuthentication()
        _isAuthenticating.value = false
    }

    fun resetAuthentication() {
        authService.reset()
        _isAuthenticating.value = false
    }

    override fun onCleared() {
        super.onCleared()
        cancelAuthentication()
    }
}