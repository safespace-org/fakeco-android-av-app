package com.fakeco.security.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.fakeco.security.BuildConfig
import com.fakeco.security.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for app settings.
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        // Set up version preference
        findPreference<Preference>("pref_version")?.summary = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        
        // Set up theme preference
        findPreference<Preference>("pref_theme")?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.setTheme(newValue.toString())
            true
        }
        
        // Set up notification preferences
        findPreference<SwitchPreferenceCompat>("pref_notifications_scan")?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.setScanNotificationsEnabled(newValue as Boolean)
            true
        }
        
        findPreference<SwitchPreferenceCompat>("pref_notifications_protection")?.setOnPreferenceChangeListener { _, newValue ->
            viewModel.setProtectionNotificationsEnabled(newValue as Boolean)
            true
        }
        
        // Set up data clearing preferences
        findPreference<Preference>("pref_clear_scan_history")?.setOnPreferenceClickListener {
            viewModel.clearScanHistory()
            showToast(getString(R.string.settings_scan_history_cleared))
            true
        }
        
        findPreference<Preference>("pref_clear_all_data")?.setOnPreferenceClickListener {
            viewModel.clearAllData()
            showToast(getString(R.string.settings_all_data_cleared))
            true
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up the view model observers
        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
                viewModel.clearToastMessage()
            }
        }
    }
    
    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }
}