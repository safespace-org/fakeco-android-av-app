package com.fakeco.security.ui.scan

/**
 * Enum representing the state of the scan.
 */
enum class ScanState {
    IDLE,       // No scan in progress
    SCANNING,   // Scan in progress
    COMPLETED   // Scan completed
}

/**
 * Data class representing scan statistics.
 *
 * @property filesScanned Number of files scanned.
 * @property threatsFound Number of threats found.
 * @property timeElapsed Formatted time elapsed (MM:SS).
 */
data class ScanStats(
    val filesScanned: Int,
    val threatsFound: Int,
    val timeElapsed: String
)

/**
 * Enum representing the severity of a threat.
 */
enum class ThreatSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Data class representing a detected threat.
 *
 * @property id Unique identifier for the threat.
 * @property name Name of the threat.
 * @property path File path where the threat was found.
 * @property severity Severity level of the threat.
 * @property isQuarantined Whether the threat has been quarantined.
 */
data class Threat(
    val id: String,
    val name: String,
    val path: String,
    val severity: ThreatSeverity,
    val isQuarantined: Boolean
)

/**
 * Data class representing the result of a scan.
 *
 * @property id Unique identifier for the scan.
 * @property timestamp Timestamp when the scan was completed.
 * @property duration Duration of the scan in seconds.
 * @property filesScanned Number of files scanned.
 * @property threatsFound Number of threats found.
 * @property threats List of detected threats.
 */
data class ScanResult(
    val id: String,
    val timestamp: Long,
    val duration: Long,
    val filesScanned: Int,
    val threatsFound: Int,
    val threats: List<Threat>
)