package com.example.movieseries.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.Series
import com.example.movieseries.data.SeriesCategory
import com.example.movieseries.databinding.ItemCategoryBinding

class SeriesCategoryAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Series, Boolean) -> Unit
) : ListAdapter<SeriesCategory, SeriesCategoryAdapter.CategoryViewHolder>(DiffCallback) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: SeriesCategory) {
            binding.categoryTitle.text = category.title

            val seriesAdapter = SeriesAdapter(fragment, onFavoriteClick)
            binding.horizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = seriesAdapter
            }
            seriesAdapter.submitList(category.series)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SeriesCategory>() {
            override fun areItemsTheSame(oldItem: SeriesCategory, newItem: SeriesCategory) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: SeriesCategory, newItem: SeriesCategory) =
                oldItem == newItem
        }
    }
}
