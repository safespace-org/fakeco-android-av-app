package com.fakeco.security.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.fakeco.security.R
import com.fakeco.security.databinding.FragmentScanBinding
import com.fakeco.security.ui.base.BaseFragment
import com.fakeco.security.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for virus scanning functionality.
 */
@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>() {

    private val viewModel: ScanViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentScanBinding {
        return FragmentScanBinding.inflate(inflater, container, attachToParent)
    }

    override fun setupUI() {
        // Set up scan button
        binding.scanButton.setOnClickListener {
            if (viewModel.isScanning()) {
                viewModel.stopScan()
            } else {
                viewModel.startScan()
            }
        }
    }

    override fun observeViewModel() {
        collectFlow(viewModel.scanState) { state ->
            updateScanUI(state)
        }
        
        collectFlow(viewModel.scanProgress) { progress ->
            binding.scanProgressBar.progress = progress
            binding.scanProgressText.text = "$progress%"
        }
        
        collectFlow(viewModel.currentFile) { file ->
            binding.currentFileText.text = file
        }
        
        collectFlow(viewModel.scanStats) { stats ->
            binding.filesScannedText.text = getString(R.string.scan_files_scanned, stats.filesScanned)
            binding.threatsFoundText.text = getString(R.string.scan_threats_found, stats.threatsFound)
            binding.timeElapsedText.text = getString(R.string.scan_time_elapsed, stats.timeElapsed)
        }
        
        collectFlow(viewModel.scanResult) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Already handled by scanState
                }
                is Resource.Success -> {
                    showScanResults(resource.data)
                }
                is Resource.Error -> {
                    showToast(resource.message)
                }
            }
        }
    }
    
    private fun updateScanUI(state: ScanState) {
        when (state) {
            ScanState.IDLE -> {
                binding.scanButton.text = getString(R.string.scan_start)
                binding.scanButton.setBackgroundColor(resources.getColor(R.color.primary, null))
                binding.scanProgressContainer.visibility = View.GONE
                binding.scanIdleContainer.visibility = View.VISIBLE
                binding.scanResultsContainer.visibility = View.GONE
            }
            ScanState.SCANNING -> {
                binding.scanButton.text = getString(R.string.scan_stop)
                binding.scanButton.setBackgroundColor(resources.getColor(R.color.status_warning, null))
                binding.scanProgressContainer.visibility = View.VISIBLE
                binding.scanIdleContainer.visibility = View.GONE
                binding.scanResultsContainer.visibility = View.GONE
            }
            ScanState.COMPLETED -> {
                binding.scanButton.text = getString(R.string.scan_new)
                binding.scanButton.setBackgroundColor(resources.getColor(R.color.primary, null))
                binding.scanProgressContainer.visibility = View.GONE
                binding.scanIdleContainer.visibility = View.GONE
                binding.scanResultsContainer.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showScanResults(result: ScanResult) {
        binding.resultTitleText.text = if (result.threatsFound > 0) {
            getString(R.string.scan_threats_found_title, result.threatsFound)
        } else {
            getString(R.string.scan_no_threats_found)
        }
        
        binding.resultDescriptionText.text = if (result.threatsFound > 0) {
            getString(R.string.scan_threats_found_description)
        } else {
            getString(R.string.scan_no_threats_description)
        }
        
        binding.resultIcon.setImageResource(
            if (result.threatsFound > 0) R.drawable.ic_warning else R.drawable.ic_check
        )
        
        // Set up threat list if there are threats
        if (result.threatsFound > 0) {
            binding.threatsList.visibility = View.VISIBLE
            // Set up adapter for threats list
            val adapter = ThreatAdapter(result.threats) { threat ->
                viewModel.quarantineThreat(threat)
            }
            binding.threatsList.adapter = adapter
        } else {
            binding.threatsList.visibility = View.GONE
        }
        
        // Set up action buttons
        binding.actionButton.setOnClickListener {
            if (result.threatsFound > 0) {
                viewModel.quarantineAllThreats()
            } else {
                // Just reset to idle state
                viewModel.resetScan()
            }
        }
        
        binding.actionButton.text = if (result.threatsFound > 0) {
            getString(R.string.scan_quarantine_all)
        } else {
            getString(R.string.scan_done)
        }
    }
}