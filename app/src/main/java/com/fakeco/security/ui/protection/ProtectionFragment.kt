package com.fakeco.security.ui.protection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fakeco.security.R
import com.fakeco.security.databinding.FragmentProtectionBinding
import com.fakeco.security.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for real-time protection functionality.
 */
@AndroidEntryPoint
class ProtectionFragment : BaseFragment<FragmentProtectionBinding>() {

    private val viewModel: ProtectionViewModel by viewModels()
    private lateinit var logAdapter: ProtectionLogAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentProtectionBinding {
        return FragmentProtectionBinding.inflate(inflater, container, attachToParent)
    }

    override fun setupUI() {
        // Set up protection toggle
        binding.protectionToggle.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setProtectionEnabled(isChecked)
        }
        
        // Set up protection level radio group
        binding.protectionLevelGroup.setOnCheckedChangeListener { _, checkedId ->
            val level = when (checkedId) {
                R.id.radioLow -> ProtectionLevel.LOW
                R.id.radioMedium -> ProtectionLevel.MEDIUM
                R.id.radioHigh -> ProtectionLevel.HIGH
                else -> ProtectionLevel.MEDIUM
            }
            viewModel.setProtectionLevel(level)
        }
        
        // Set up log adapter
        logAdapter = ProtectionLogAdapter()
        binding.protectionLogList.adapter = logAdapter
    }

    override fun observeViewModel() {
        collectFlow(viewModel.protectionEnabled) { isEnabled ->
            binding.protectionToggle.isChecked = isEnabled
            updateProtectionUI(isEnabled)
        }
        
        collectFlow(viewModel.protectionLevel) { level ->
            val radioId = when (level) {
                ProtectionLevel.LOW -> R.id.radioLow
                ProtectionLevel.MEDIUM -> R.id.radioMedium
                ProtectionLevel.HIGH -> R.id.radioHigh
            }
            binding.protectionLevelGroup.check(radioId)
        }
        
        collectFlow(viewModel.protectionStats) { stats ->
            binding.statsThreatsBlockedText.text = stats.threatsBlocked.toString()
            binding.statsAppsScannedText.text = stats.appsScanned.toString()
            binding.statsLastUpdatedText.text = stats.lastUpdated
        }
        
        collectFlow(viewModel.protectionLogs) { logs ->
            logAdapter.submitList(logs)
            if (logs.isNotEmpty()) {
                binding.emptyLogText.visibility = View.GONE
            } else {
                binding.emptyLogText.visibility = View.VISIBLE
            }
        }
    }
    
    private fun updateProtectionUI(isEnabled: Boolean) {
        if (isEnabled) {
            binding.protectionStatusText.text = getString(R.string.protection_enabled)
            binding.protectionStatusText.setTextColor(resources.getColor(R.color.status_safe, null))
            binding.protectionStatusIcon.setImageResource(R.drawable.ic_shield_check)
            binding.protectionStatusIcon.setColorFilter(resources.getColor(R.color.status_safe, null))
            binding.protectionLevelContainer.visibility = View.VISIBLE
            binding.protectionShieldAnimation.visibility = View.VISIBLE
            binding.protectionShieldAnimation.playAnimation()
        } else {
            binding.protectionStatusText.text = getString(R.string.protection_disabled)
            binding.protectionStatusText.setTextColor(resources.getColor(R.color.status_danger, null))
            binding.protectionStatusIcon.setImageResource(R.drawable.ic_shield_off)
            binding.protectionStatusIcon.setColorFilter(resources.getColor(R.color.status_danger, null))
            binding.protectionLevelContainer.visibility = View.GONE
            binding.protectionShieldAnimation.visibility = View.GONE
            binding.protectionShieldAnimation.pauseAnimation()
        }
    }
}