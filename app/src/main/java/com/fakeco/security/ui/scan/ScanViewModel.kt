package com.fakeco.security.ui.scan

import androidx.lifecycle.viewModelScope
import com.fakeco.security.ui.base.BaseViewModel
import com.fakeco.security.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Scan screen.
 */
@HiltViewModel
class ScanViewModel @Inject constructor() : BaseViewModel() {

    private val _scanState = MutableStateFlow(ScanState.IDLE)
    val scanState: StateFlow<ScanState> = _scanState

    private val _scanProgress = MutableStateFlow(0)
    val scanProgress: StateFlow<Int> = _scanProgress

    private val _currentFile = MutableStateFlow("")
    val currentFile: StateFlow<String> = _currentFile

    private val _scanStats = MutableStateFlow(ScanStats(0, 0, "00:00"))
    val scanStats: StateFlow<ScanStats> = _scanStats

    private val _scanResult = MutableStateFlow<Resource<ScanResult>>(Resource.Loading)
    val scanResult: Flow<Resource<ScanResult>> = _scanResult

    private var scanJob: Job? = null
    private val random = Random()
    private val mockFilePaths = listOf(
        "/storage/emulated/0/Download/document.pdf",
        "/storage/emulated/0/DCIM/Camera/IMG_20250101_123456.jpg",
        "/storage/emulated/0/Android/data/com.example.app/cache/temp.dat",
        "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20250101-WA0001.jpg",
        "/storage/emulated/0/Documents/report.docx",
        "/data/data/com.fakeco.security/shared_prefs/settings.xml",
        "/data/data/com.fakeco.security/databases/security.db",
        "/system/app/Calculator/Calculator.apk",
        "/storage/emulated/0/Music/song.mp3",
        "/storage/emulated/0/Movies/video.mp4"
    )
    
    private val mockThreatNames = listOf(
        "Trojan.AndroidOS.Agent.a",
        "Adware.AndroidOS.Notifier.b",
        "Spyware.AndroidOS.Tracker.c",
        "Ransomware.AndroidOS.Locker.d",
        "Malware.AndroidOS.Banker.e",
        "Backdoor.AndroidOS.Access.f",
        "Worm.AndroidOS.Spread.g",
        "Rootkit.AndroidOS.Privilege.h",
        "Virus.AndroidOS.Infector.i",
        "PUP.AndroidOS.Unwanted.j"
    )
    
    private val mockThreatPaths = listOf(
        "/storage/emulated/0/Download/infected_file.apk",
        "/storage/emulated/0/Download/suspicious_document.pdf",
        "/storage/emulated/0/Android/data/com.suspicious.app/files/malicious.dex",
        "/data/data/com.malicious.app/shared_prefs/tracking.xml",
        "/storage/emulated/0/WhatsApp/Media/WhatsApp Documents/suspicious_attachment.pdf"
    )

    /**
     * Starts a new scan.
     */
    fun startScan() {
        if (_scanState.value == ScanState.SCANNING) return
        
        _scanState.value = ScanState.SCANNING
        _scanProgress.value = 0
        _currentFile.value = ""
        _scanStats.value = ScanStats(0, 0, "00:00")
        _scanResult.value = Resource.Loading
        
        scanJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var filesScanned = 0
            var threatsFound = 0
            val threats = mutableListOf<Threat>()
            
            // Simulate scanning process
            for (progress in 1..100) {
                if (!isActive) break
                
                _scanProgress.value = progress
                
                // Update current file being scanned
                if (progress % 5 == 0) {
                    val randomFileIndex = random.nextInt(mockFilePaths.size)
                    _currentFile.value = mockFilePaths[randomFileIndex]
                    filesScanned++
                }
                
                // Randomly detect threats (about 5% chance per file)
                if (progress % 20 == 0 && random.nextInt(100) < 5) {
                    threatsFound++
                    val threatIndex = random.nextInt(mockThreatNames.size)
                    val pathIndex = random.nextInt(mockThreatPaths.size)
                    
                    threats.add(
                        Threat(
                            id = UUID.randomUUID().toString(),
                            name = mockThreatNames[threatIndex],
                            path = mockThreatPaths[pathIndex],
                            severity = ThreatSeverity.values()[random.nextInt(ThreatSeverity.values().size)],
                            isQuarantined = false
                        )
                    )
                }
                
                // Update scan stats
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                val timeElapsed = String.format("%02d:%02d", minutes, seconds)
                
                _scanStats.value = ScanStats(
                    filesScanned = filesScanned,
                    threatsFound = threatsFound,
                    timeElapsed = timeElapsed
                )
                
                delay(100) // Simulate scanning delay
            }
            
            // Scan completed
            if (isActive) {
                _scanState.value = ScanState.COMPLETED
                _scanResult.value = Resource.Success(
                    ScanResult(
                        id = UUID.randomUUID().toString(),
                        timestamp = System.currentTimeMillis(),
                        duration = (System.currentTimeMillis() - startTime) / 1000,
                        filesScanned = filesScanned,
                        threatsFound = threatsFound,
                        threats = threats
                    )
                )
            }
        }
    }
    
    /**
     * Stops the current scan.
     */
    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _scanState.value = ScanState.IDLE
    }
    
    /**
     * Resets the scan state to idle.
     */
    fun resetScan() {
        _scanState.value = ScanState.IDLE
        _scanProgress.value = 0
        _currentFile.value = ""
        _scanStats.value = ScanStats(0, 0, "00:00")
        _scanResult.value = Resource.Loading
    }
    
    /**
     * Quarantines a specific threat.
     */
    fun quarantineThreat(threat: Threat) {
        val currentResult = (_scanResult.value as? Resource.Success)?.data ?: return
        
        val updatedThreats = currentResult.threats.map {
            if (it.id == threat.id) it.copy(isQuarantined = true) else it
        }
        
        _scanResult.value = Resource.Success(
            currentResult.copy(threats = updatedThreats)
        )
        
        // Show toast message
        sendEvent("${threat.name} has been quarantined")
    }
    
    /**
     * Quarantines all threats.
     */
    fun quarantineAllThreats() {
        val currentResult = (_scanResult.value as? Resource.Success)?.data ?: return
        
        val updatedThreats = currentResult.threats.map {
            it.copy(isQuarantined = true)
        }
        
        _scanResult.value = Resource.Success(
            currentResult.copy(threats = updatedThreats)
        )
        
        // Show toast message
        sendEvent("All threats have been quarantined")
        
        // Reset to idle state after a delay
        viewModelScope.launch {
            delay(2000)
            resetScan()
        }
    }
    
    /**
     * Checks if a scan is currently in progress.
     */
    fun isScanning(): Boolean {
        return _scanState.value == ScanState.SCANNING
    }
    
    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
    }
}