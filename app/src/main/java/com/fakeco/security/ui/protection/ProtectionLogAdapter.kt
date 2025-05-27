package com.fakeco.security.ui.protection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fakeco.security.R
import com.fakeco.security.databinding.ItemProtectionLogBinding

/**
 * Adapter for displaying protection log entries in a RecyclerView.
 */
class ProtectionLogAdapter : ListAdapter<ProtectionLog, ProtectionLogAdapter.LogViewHolder>(LogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemProtectionLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LogViewHolder(
        private val binding: ItemProtectionLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: ProtectionLog) {
            binding.logTimeText.text = log.formattedTime
            binding.logThreatText.text = log.threatName
            binding.logSourceText.text = log.source
            binding.logActionText.text = log.action
            
            // Set severity color
            val color = when (log.severity) {
                ThreatSeverity.LOW -> R.color.status_info
                ThreatSeverity.MEDIUM -> R.color.status_warning
                ThreatSeverity.HIGH -> R.color.status_danger
                ThreatSeverity.CRITICAL -> R.color.status_critical
            }
            
            binding.logSeverityIndicator.setColorFilter(
                ContextCompat.getColor(binding.root.context, color)
            )
        }
    }

    class LogDiffCallback : DiffUtil.ItemCallback<ProtectionLog>() {
        override fun areItemsTheSame(oldItem: ProtectionLog, newItem: ProtectionLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProtectionLog, newItem: ProtectionLog): Boolean {
            return oldItem == newItem
        }
    }
}