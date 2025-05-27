package com.fakeco.security.ui.protection

/**
 * Enum representing the protection level.
 */
enum class ProtectionLevel {
    LOW,
    MEDIUM,
    HIGH
}

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
 * Data class representing protection statistics.
 *
 * @property threatsBlocked Number of threats blocked.
 * @property appsScanned Number of apps scanned.
 * @property lastUpdated Formatted timestamp of the last update.
 */
data class ProtectionStats(
    val threatsBlocked: Int,
    val appsScanned: Int,
    val lastUpdated: String
)

/**
 * Data class representing a protection log entry.
 *
 * @property id Unique identifier for the log entry.
 * @property timestamp Timestamp when the event occurred.
 * @property formattedTime Formatted timestamp string.
 * @property threatName Name of the detected threat.
 * @property source Source of the threat (app name).
 * @property action Action taken (blocked, quarantined, etc.).
 * @property severity Severity level of the threat.
 */
data class ProtectionLog(
    val id: String,
    val timestamp: Long,
    val formattedTime: String,
    val threatName: String,
    val source: String,
    val action: String,
    val severity: ThreatSeverity
)