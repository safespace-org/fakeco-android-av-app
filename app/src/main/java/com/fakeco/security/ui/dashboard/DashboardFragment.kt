package com.fakeco.security.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fakeco.security.R
import com.fakeco.security.databinding.FragmentDashboardBinding
import com.fakeco.security.ui.base.BaseFragment
import com.fakeco.security.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dashboard fragment that displays the security status and quick actions.
 */
@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentDashboardBinding {
        return FragmentDashboardBinding.inflate(inflater, container, attachToParent)
    }

    override fun setupUI() {
        // Set up click listeners
        binding.cardScan.setOnClickListener {
            findNavController().navigate(R.id.scanFragment)
        }
        
        binding.cardProtection.setOnClickListener {
            findNavController().navigate(R.id.protectionFragment)
        }
        
        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        
        // Set up swipe refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshDashboard()
        }
    }

    override fun observeViewModel() {
        collectFlow(viewModel.securityStatus) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    updateSecurityStatus(resource.data)
                }
                is Resource.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    showToast(resource.message)
                }
            }
        }
        
        collectFlow(viewModel.lastScanInfo) { resource ->
            if (resource is Resource.Success) {
                updateLastScanInfo(resource.data)
            }
        }
        
        collectFlow(viewModel.errorEvent) { errorMessage ->
            showToast(errorMessage)
        }
    }
    
    private fun updateSecurityStatus(status: SecurityStatus) {
        // Update security status UI
        binding.securityScore.text = "${status.score}%"
        
        // Update status text and color based on score
        when {
            status.score >= 80 -> {
                binding.securityStatusText.text = getString(R.string.dashboard_protected)
                binding.securityStatusText.setTextColor(resources.getColor(R.color.status_safe, null))
                binding.securityScoreCard.setCardBackgroundColor(resources.getColor(R.color.status_safe, null))
            }
            status.score >= 50 -> {
                binding.securityStatusText.text = getString(R.string.dashboard_at_risk)
                binding.securityStatusText.setTextColor(resources.getColor(R.color.status_warning, null))
                binding.securityScoreCard.setCardBackgroundColor(resources.getColor(R.color.status_warning, null))
            }
            else -> {
                binding.securityStatusText.text = getString(R.string.dashboard_at_risk)
                binding.securityStatusText.setTextColor(resources.getColor(R.color.status_danger, null))
                binding.securityScoreCard.setCardBackgroundColor(resources.getColor(R.color.status_danger, null))
            }
        }
        
        // Update feature status indicators
        binding.scanStatusIndicator.isActivated = status.scanEnabled
        binding.protectionStatusIndicator.isActivated = status.protectionEnabled
        binding.vpnStatusIndicator.isActivated = status.vpnEnabled
        binding.webProtectionStatusIndicator.isActivated = status.webProtectionEnabled
    }
    
    private fun updateLastScanInfo(scanInfo: LastScanInfo) {
        binding.lastScanText.text = getString(R.string.dashboard_last_scan, scanInfo.formattedDate)
        
        if (scanInfo.threatsFound > 0) {
            binding.threatsFoundText.text = getString(R.string.dashboard_threats_found, scanInfo.threatsFound)
            binding.threatsFoundText.setTextColor(resources.getColor(R.color.status_danger, null))
        } else {
            binding.threatsFoundText.text = getString(R.string.dashboard_no_threats)
            binding.threatsFoundText.setTextColor(resources.getColor(R.color.status_safe, null))
        }
    }
}