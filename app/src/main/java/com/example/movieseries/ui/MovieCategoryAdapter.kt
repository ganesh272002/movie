package com.example.movieseries.ui

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.data.Movie
import com.example.movieseries.data.MovieCategory
import com.example.movieseries.databinding.ItemCategoryBinding


class MovieCategoryAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Movie, Boolean) -> Unit,
    private val isLastPage: () -> Boolean,
    private val isLoading: () -> Boolean,
    private val onLoadMore: () -> Unit
) : ListAdapter<MovieCategory, MovieCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    private val scrollStates = hashMapOf<Int, Parcelable?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: CategoryViewHolder) {
        super.onViewRecycled(holder)
        holder.saveScrollState()
    }

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: MovieCategory) {
            binding.categoryTitle.text = category.title

            val layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.horizontalRecyclerView.layoutManager = layoutManager

            val adapter = MoviesAdapter(fragment, onFavoriteClick)
            binding.horizontalRecyclerView.adapter = adapter
            adapter.setItems(category.movies)

            // restore scroll state if available
            scrollStates[bindingAdapterPosition]?.let {
                layoutManager.onRestoreInstanceState(it)
            }

            // attach pagination listener only for popular movies row
            if (category.title == "Popular Movies") {
                binding.horizontalRecyclerView.clearOnScrollListeners()
                binding.horizontalRecyclerView.addOnScrollListener(object :
                    PaginationScrollListener(layoutManager) {
                    override fun isLastPage() = this@MovieCategoryAdapter.isLastPage()
                    override fun isLoading() = this@MovieCategoryAdapter.isLoading()
                    override fun loadMoreItems() = onLoadMore()
                })
            }
        }

        fun saveScrollState() {
            val lm = binding.horizontalRecyclerView.layoutManager
            scrollStates[bindingAdapterPosition] = lm?.onSaveInstanceState()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieCategory>() {
        override fun areItemsTheSame(oldItem: MovieCategory, newItem: MovieCategory) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: MovieCategory, newItem: MovieCategory) =
            oldItem == newItem
    }
}
