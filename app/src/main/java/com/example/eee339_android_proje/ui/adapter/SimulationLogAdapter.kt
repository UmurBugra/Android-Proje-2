package com.example.eee339_android_proje.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eee339_android_proje.data.entity.SimulationLog
import com.example.eee339_android_proje.databinding.ItemSimulationLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SimulationLogAdapter : ListAdapter<SimulationLog, SimulationLogAdapter.LogViewHolder>(LogDiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemSimulationLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LogViewHolder(
        private val binding: ItemSimulationLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: SimulationLog) {
            binding.tvTimestamp.text = timeFormat.format(Date(log.timestamp))
            binding.tvActionName.text = log.actionName
            binding.tvFeedback.text = log.feedbackMessage
        }
    }

    private class LogDiffCallback : DiffUtil.ItemCallback<SimulationLog>() {
        override fun areItemsTheSame(oldItem: SimulationLog, newItem: SimulationLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimulationLog, newItem: SimulationLog): Boolean {
            return oldItem == newItem
        }
    }
}
