package com.example.eee339_android_proje.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eee339_android_proje.data.entity.Classroom
import com.example.eee339_android_proje.databinding.ItemClassroomBinding

class ClassroomAdapter(
    private val showJoinCode: Boolean = false,
    private val onItemClick: (Classroom) -> Unit
) : ListAdapter<Classroom, ClassroomAdapter.ClassroomViewHolder>(ClassroomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val binding = ItemClassroomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassroomViewHolder(
        private val binding: ItemClassroomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(classroom: Classroom) {
            binding.tvClassName.text = classroom.className
            binding.tvDescription.text = classroom.description
            
            // Öğretmen için katılım kodunu göster
            if (showJoinCode && classroom.joinCode.isNotEmpty()) {
                binding.tvJoinCode.visibility = View.VISIBLE
                binding.tvJoinCode.text = classroom.joinCode
            } else {
                binding.tvJoinCode.visibility = View.GONE
            }
        }
    }

    private class ClassroomDiffCallback : DiffUtil.ItemCallback<Classroom>() {
        override fun areItemsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Classroom, newItem: Classroom): Boolean {
            return oldItem == newItem
        }
    }
}

