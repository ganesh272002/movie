package com.example.movieseries.ui

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.movieseries.data.Movie
import com.example.movieseries.data.MovieCategory
import com.example.movieseries.databinding.ItemCategoryBinding
import com.example.movieseries.viewmodel.HomeViewModel
import kotlin.math.abs


class MovieCategoryAdapter(
    private val fragment: Fragment,
    private val viewModel: HomeViewModel,
    private val onFavoriteClick: (Movie, Boolean) -> Unit,
    private val isLastPage: (String) -> Boolean,
    private val isLoading: (String) -> Boolean,
    private val onLoadMore: (String) -> Unit
) : ListAdapter<MovieCategory, MovieCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    private val scrollStates = hashMapOf<Int, Parcelable?>()

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: MovieCategory) {
            binding.categoryTitle.text = category.title

            val layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.horizontalRecyclerView.layoutManager = layoutManager

            val adapter = MoviesAdapter(fragment, onFavoriteClick)
            binding.horizontalRecyclerView.adapter = adapter
            adapter.submitList(category.movies)

            scrollStates[bindingAdapterPosition]?.let { layoutManager.onRestoreInstanceState(it) }

            binding.horizontalRecyclerView.clearOnScrollListeners()
            binding.horizontalRecyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
                override fun isLastPage() = this@MovieCategoryAdapter.isLastPage(category.title)
                override fun isLoading() = this@MovieCategoryAdapter.isLoading(category.title)
                override fun loadMoreItems() = onLoadMore(category.title)
            })

//viewPagerDisable

            binding.horizontalRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                var startX = 0f
                var startY = 0f
                var isScrollingHorizontally = false

                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            startX = e.x
                            startY = e.y
                            isScrollingHorizontally = false

                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = e.x - startX
                            val dy = e.y - startY

                            if (!isScrollingHorizontally) {


                                if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                    isScrollingHorizontally = true
                                    rv.parent.requestDisallowInterceptTouchEvent(true)
                                } else if (kotlin.math.abs(dy) > kotlin.math.abs(dx)) {

                                    rv.parent.requestDisallowInterceptTouchEvent(false)
                                }
                            } else {

                                rv.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            rv.parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })





        }

        fun saveScrollState() {
            val lm = binding.horizontalRecyclerView.layoutManager
            scrollStates[bindingAdapterPosition] = lm?.onSaveInstanceState()
        }
    }

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

    class DiffCallback : DiffUtil.ItemCallback<MovieCategory>() {
        override fun areItemsTheSame(oldItem: MovieCategory, newItem: MovieCategory) = oldItem.title == newItem.title
        override fun areContentsTheSame(oldItem: MovieCategory, newItem: MovieCategory) = oldItem == newItem
    }

    fun updateSavedMovies(savedIds: Set<Int>) {
        val updatedList = currentList.map { category ->
            category.copy(
                movies = category.movies.map { movie -> movie.copy(isSaved = savedIds.contains(movie.id)) }
            )
        }
        submitList(updatedList)
    }
}
