package com.fakeco.security.ui.protection

import androidx.lifecycle.viewModelScope
import com.fakeco.security.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Protection screen.
 */
@HiltViewModel
class ProtectionViewModel @Inject constructor() : BaseViewModel() {

    private val _protectionEnabled = MutableStateFlow(false)
    val protectionEnabled: StateFlow<Boolean> = _protectionEnabled

    private val _protectionLevel = MutableStateFlow(ProtectionLevel.MEDIUM)
    val protectionLevel: StateFlow<ProtectionLevel> = _protectionLevel

    private val _protectionStats = MutableStateFlow(
        ProtectionStats(
            threatsBlocked = 0,
            appsScanned = 0,
            lastUpdated = "Never"
        )
    )
    val protectionStats: StateFlow<ProtectionStats> = _protectionStats

    private val _protectionLogs = MutableStateFlow<List<ProtectionLog>>(emptyList())
    val protectionLogs: StateFlow<List<ProtectionLog>> = _protectionLogs

    private var simulationJob: Job? = null
    private val random = Random()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    private val mockAppNames = listOf(
        "Social Media App",
        "Photo Editor Pro",
        "Free Games Hub",
        "Weather Forecast",
        "Flashlight Ultra",
        "Battery Saver",
        "Video Player HD",
        "Music Streaming",
        "File Manager",
        "QR Code Scanner"
    )
    
    private val mockThreatNames = listOf(
        "Trojan.AndroidOS.Agent.a",
        "Adware.AndroidOS.Notifier.b",
        "Spyware.AndroidOS.Tracker.c",
        "Ransomware.AndroidOS.Locker.d",
        "Malware.AndroidOS.Banker.e"
    )
    
    private val mockActions = listOf(
        "Blocked",
        "Quarantined",
        "Removed",
        "Isolated"
    )

    init {
        // Check if protection was previously enabled
        // In a real app, this would come from a repository
        _protectionEnabled.value = false
        _protectionLevel.value = ProtectionLevel.MEDIUM
    }

    /**
     * Enables or disables real-time protection.
     */
    fun setProtectionEnabled(enabled: Boolean) {
        if (_protectionEnabled.value == enabled) return
        
        _protectionEnabled.value = enabled
        
        if (enabled) {
            startProtectionSimulation()
            
            // Update last updated time
            val currentStats = _protectionStats.value
            _protectionStats.value = currentStats.copy(
                lastUpdated = dateFormat.format(Date())
            )
        } else {
            stopProtectionSimulation()
        }
    }
    
    /**
     * Sets the protection level.
     */
    fun setProtectionLevel(level: ProtectionLevel) {
        if (_protectionLevel.value == level) return
        
        _protectionLevel.value = level
        
        // If protection is enabled, restart simulation with new level
        if (_protectionEnabled.value) {
            stopProtectionSimulation()
            startProtectionSimulation()
        }
    }
    
    /**
     * Starts the protection simulation.
     */
    private fun startProtectionSimulation() {
        simulationJob?.cancel()
        
        simulationJob = viewModelScope.launch {
            // Initial delay before first event
            delay(2000)
            
            while (true) {
                // Generate random protection events based on protection level
                val eventChance = when (_protectionLevel.value) {
                    ProtectionLevel.LOW -> 30 // 30% chance
                    ProtectionLevel.MEDIUM -> 50 // 50% chance
                    ProtectionLevel.HIGH -> 70 // 70% chance
                }
                
                if (random.nextInt(100) < eventChance) {
                    generateProtectionEvent()
                }
                
                // Scan apps periodically
                if (random.nextInt(100) < 20) { // 20% chance
                    scanRandomApp()
                }
                
                // Delay between events (5-15 seconds)
                val delayTime = 5000L + random.nextInt(10000)
                delay(delayTime)
            }
        }
    }
    
    /**
     * Stops the protection simulation.
     */
    private fun stopProtectionSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }
    
    /**
     * Generates a random protection event.
     */
    private fun generateProtectionEvent() {
        // Generate random threat
        val threatIndex = random.nextInt(mockThreatNames.size)
        val threatName = mockThreatNames[threatIndex]
        
        // Generate random app
        val appIndex = random.nextInt(mockAppNames.size)
        val appName = mockAppNames[appIndex]
        
        // Generate random action
        val actionIndex = random.nextInt(mockActions.size)
        val action = mockActions[actionIndex]
        
        // Create log entry
        val log = ProtectionLog(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            formattedTime = dateFormat.format(Date()),
            threatName = threatName,
            source = appName,
            action = action,
            severity = ThreatSeverity.values()[random.nextInt(ThreatSeverity.values().size)]
        )
        
        // Update logs
        val currentLogs = _protectionLogs.value.toMutableList()
        currentLogs.add(0, log) // Add to beginning of list
        
        // Keep only the last 50 logs
        if (currentLogs.size > 50) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        
        _protectionLogs.value = currentLogs
        
        // Update stats
        val currentStats = _protectionStats.value
        _protectionStats.value = currentStats.copy(
            threatsBlocked = currentStats.threatsBlocked + 1,
            lastUpdated = dateFormat.format(Date())
        )
        
        // Send notification
        sendEvent("Threat detected: $threatName from $appName")
    }
    
    /**
     * Simulates scanning a random app.
     */
    private fun scanRandomApp() {
        // Generate random app
        val appIndex = random.nextInt(mockAppNames.size)
        val appName = mockAppNames[appIndex]
        
        // Update stats
        val currentStats = _protectionStats.value
        _protectionStats.value = currentStats.copy(
            appsScanned = currentStats.appsScanned + 1,
            lastUpdated = dateFormat.format(Date())
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}