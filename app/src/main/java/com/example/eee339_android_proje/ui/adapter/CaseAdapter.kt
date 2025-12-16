package com.example.eee339_android_proje.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eee339_android_proje.data.entity.CaseScenario
import com.example.eee339_android_proje.databinding.ItemCaseBinding

class CaseAdapter(
    private val onItemClick: (CaseScenario) -> Unit
) : ListAdapter<CaseScenario, CaseAdapter.CaseViewHolder>(CaseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseViewHolder {
        val binding = ItemCaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CaseViewHolder(
        private val binding: ItemCaseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(caseScenario: CaseScenario) {
            binding.tvCaseTitle.text = caseScenario.title
            binding.tvDiagnosis.text = caseScenario.correctDiagnosis
        }
    }

    private class CaseDiffCallback : DiffUtil.ItemCallback<CaseScenario>() {
        override fun areItemsTheSame(oldItem: CaseScenario, newItem: CaseScenario): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CaseScenario, newItem: CaseScenario): Boolean {
            return oldItem == newItem
        }
    }
}
