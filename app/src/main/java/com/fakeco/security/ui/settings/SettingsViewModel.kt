package com.fakeco.security.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    /**
     * Sets the app theme.
     *
     * @param theme The theme to set ("light", "dark", or "system").
     */
    fun setTheme(theme: String) {
        // In a real app, this would save the theme preference and apply it
        _toastMessage.value = "Theme set to $theme"
    }

    /**
     * Enables or disables scan notifications.
     *
     * @param enabled Whether scan notifications should be enabled.
     */
    fun setScanNotificationsEnabled(enabled: Boolean) {
        // In a real app, this would update the notification settings
        _toastMessage.value = if (enabled) {
            "Scan notifications enabled"
        } else {
            "Scan notifications disabled"
        }
    }

    /**
     * Enables or disables protection notifications.
     *
     * @param enabled Whether protection notifications should be enabled.
     */
    fun setProtectionNotificationsEnabled(enabled: Boolean) {
        // In a real app, this would update the notification settings
        _toastMessage.value = if (enabled) {
            "Protection notifications enabled"
        } else {
            "Protection notifications disabled"
        }
    }

    /**
     * Clears scan history.
     */
    fun clearScanHistory() {
        // In a real app, this would clear the scan history from the database
        _toastMessage.value = "Scan history cleared"
    }

    /**
     * Clears all app data.
     */
    fun clearAllData() {
        // In a real app, this would clear all user data
        _toastMessage.value = "All data cleared"
    }

    /**
     * Clears the toast message.
     */
    fun clearToastMessage() {
        _toastMessage.value = ""
    }
}