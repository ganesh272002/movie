package com.example.movieseries.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.ItemSavedBinding

class SavedItemsAdapter(
    private val onRemoveClick: (SavedItemEntity) -> Unit,
    private val onItemLongClick: (SavedItemEntity) -> Unit
) : ListAdapter<SavedItemEntity, SavedItemsAdapter.SavedViewHolder>(DiffCallback) {

    inner class SavedViewHolder(private val binding: ItemSavedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SavedItemEntity) {
            binding.item = item


            binding.onHeartClick = View.OnClickListener {
                onRemoveClick(item)
            }


            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemSavedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<SavedItemEntity>() {
        override fun areItemsTheSame(oldItem: SavedItemEntity, newItem: SavedItemEntity) =
            oldItem.id == newItem.id && oldItem.type == newItem.type

        override fun areContentsTheSame(oldItem: SavedItemEntity, newItem: SavedItemEntity) =
            oldItem == newItem
    }
}
