package com.fakeco.security.ui.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fakeco.security.R
import com.fakeco.security.databinding.ItemThreatBinding

/**
 * Adapter for displaying threats in a RecyclerView.
 *
 * @property threats List of threats to display.
 * @property onQuarantineClick Callback for when the quarantine button is clicked.
 */
class ThreatAdapter(
    private val threats: List<Threat>,
    private val onQuarantineClick: (Threat) -> Unit
) : RecyclerView.Adapter<ThreatAdapter.ThreatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreatViewHolder {
        val binding = ItemThreatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ThreatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThreatViewHolder, position: Int) {
        holder.bind(threats[position])
    }

    override fun getItemCount(): Int = threats.size

    inner class ThreatViewHolder(
        private val binding: ItemThreatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(threat: Threat) {
            binding.threatNameText.text = threat.name
            binding.threatPathText.text = threat.path
            
            // Set severity color and text
            val (color, text) = when (threat.severity) {
                ThreatSeverity.LOW -> Pair(
                    R.color.status_info,
                    binding.root.context.getString(R.string.threat_severity_low)
                )
                ThreatSeverity.MEDIUM -> Pair(
                    R.color.status_warning,
                    binding.root.context.getString(R.string.threat_severity_medium)
                )
                ThreatSeverity.HIGH -> Pair(
                    R.color.status_danger,
                    binding.root.context.getString(R.string.threat_severity_high)
                )
                ThreatSeverity.CRITICAL -> Pair(
                    R.color.status_critical,
                    binding.root.context.getString(R.string.threat_severity_critical)
                )
            }
            
            binding.threatSeverityText.text = text
            binding.threatSeverityText.setTextColor(
                ContextCompat.getColor(binding.root.context, color)
            )
            
            // Set quarantine button state
            if (threat.isQuarantined) {
                binding.quarantineButton.text = binding.root.context.getString(R.string.threat_quarantined)
                binding.quarantineButton.isEnabled = false
                binding.quarantineButton.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.status_safe)
                )
            } else {
                binding.quarantineButton.text = binding.root.context.getString(R.string.threat_quarantine)
                binding.quarantineButton.isEnabled = true
                binding.quarantineButton.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary)
                )
                binding.quarantineButton.setOnClickListener {
                    onQuarantineClick(threat)
                }
            }
        }
    }
}