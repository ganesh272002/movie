package com.example.movieseries.ui

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieseries.data.Movie
import com.example.movieseries.data.MovieCategory
import com.example.movieseries.databinding.ItemCategoryBinding

class MovieCategoryAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Movie, Boolean) -> Unit,
    private val onLoadMore: () -> Unit // horizontal pagination callback
) : ListAdapter<MovieCategory, MovieCategoryAdapter.CategoryViewHolder>(DiffCallback) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var horizontalScrollState: Parcelable? = null

        fun bind(category: MovieCategory) {
            binding.categoryTitle.text = category.title

            val movieAdapter = MoviesAdapter(fragment, onFavoriteClick)
            val layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.horizontalRecyclerView.layoutManager = layoutManager
            binding.horizontalRecyclerView.adapter = movieAdapter

            // Restore scroll position
            horizontalScrollState?.let { layoutManager.onRestoreInstanceState(it) }

            movieAdapter.submitList(category.movies)

            // Add horizontal pagination only for "Popular Movies"
            if (category.title == "Popular Movies") {
                binding.horizontalRecyclerView.addOnScrollListener(
                    object : PaginationScrollListener(layoutManager) {
                        override fun isLastPage() = false
                        override fun isLoading() = false
                        override fun loadMoreItems() {
                            onLoadMore()
                        }
                    }
                )
            }
        }

        fun saveScrollState() {
            horizontalScrollState = binding.horizontalRecyclerView.layoutManager?.onSaveInstanceState()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MovieCategory>() {
            override fun areItemsTheSame(oldItem: MovieCategory, newItem: MovieCategory) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: MovieCategory, newItem: MovieCategory) =
                oldItem == newItem
        }
    }
}
