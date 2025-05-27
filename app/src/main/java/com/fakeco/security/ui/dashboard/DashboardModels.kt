package com.fakeco.security.ui.dashboard

import java.util.Date

/**
 * Data class representing the security status of the device.
 *
 * @property score The overall security score (0-100).
 * @property scanEnabled Whether the scan feature is enabled.
 * @property protectionEnabled Whether the real-time protection is enabled.
 * @property vpnEnabled Whether the VPN is enabled.
 * @property webProtectionEnabled Whether the web protection is enabled.
 */
data class SecurityStatus(
    val score: Int,
    val scanEnabled: Boolean,
    val protectionEnabled: Boolean,
    val vpnEnabled: Boolean,
    val webProtectionEnabled: Boolean
)

/**
 * Data class representing information about the last scan.
 *
 * @property date The date of the last scan.
 * @property formattedDate The formatted date string.
 * @property threatsFound The number of threats found during the scan.
 */
data class LastScanInfo(
    val date: Date,
    val formattedDate: String,
    val threatsFound: Int
)