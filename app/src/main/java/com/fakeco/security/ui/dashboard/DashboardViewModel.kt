package com.fakeco.security.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.fakeco.security.ui.base.BaseViewModel
import com.fakeco.security.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor() : BaseViewModel() {

    private val _securityStatus = MutableStateFlow<Resource<SecurityStatus>>(Resource.Loading)
    val securityStatus: Flow<Resource<SecurityStatus>> = _securityStatus

    private val _lastScanInfo = MutableStateFlow<Resource<LastScanInfo>>(Resource.Loading)
    val lastScanInfo: Flow<Resource<LastScanInfo>> = _lastScanInfo

    private val random = Random()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    init {
        refreshDashboard()
    }

    /**
     * Refreshes the dashboard data.
     */
    fun refreshDashboard() {
        viewModelScope.launch {
            _securityStatus.value = Resource.Loading
            _lastScanInfo.value = Resource.Loading
            
            // Simulate network delay
            delay(1000)
            
            try {
                // Generate mock security status
                val securityStatus = generateMockSecurityStatus()
                _securityStatus.value = Resource.Success(securityStatus)
                
                // Generate mock last scan info
                val lastScanInfo = generateMockLastScanInfo()
                _lastScanInfo.value = Resource.Success(lastScanInfo)
            } catch (e: Exception) {
                _securityStatus.value = Resource.Error("Failed to load security status: ${e.message}")
                _lastScanInfo.value = Resource.Error("Failed to load scan info: ${e.message}")
            }
        }
    }
    
    /**
     * Generates mock security status data.
     */
    private fun generateMockSecurityStatus(): SecurityStatus {
        val scanEnabled = random.nextBoolean()
        val protectionEnabled = random.nextBoolean()
        val vpnEnabled = random.nextBoolean()
        val webProtectionEnabled = random.nextBoolean()
        
        // Calculate score based on enabled features
        var score = 0
        if (scanEnabled) score += 25
        if (protectionEnabled) score += 25
        if (vpnEnabled) score += 25
        if (webProtectionEnabled) score += 25
        
        return SecurityStatus(
            score = score,
            scanEnabled = scanEnabled,
            protectionEnabled = protectionEnabled,
            vpnEnabled = vpnEnabled,
            webProtectionEnabled = webProtectionEnabled
        )
    }
    
    /**
     * Generates mock last scan information.
     */
    private fun generateMockLastScanInfo(): LastScanInfo {
        // Generate a random date within the last 7 days
        val currentTime = System.currentTimeMillis()
        val randomOffset = random.nextInt(7 * 24 * 60 * 60 * 1000)
        val scanDate = Date(currentTime - randomOffset)
        
        // Generate random number of threats (0-5)
        val threatsFound = random.nextInt(6)
        
        return LastScanInfo(
            date = scanDate,
            formattedDate = dateFormat.format(scanDate),
            threatsFound = threatsFound
        )
    }
}