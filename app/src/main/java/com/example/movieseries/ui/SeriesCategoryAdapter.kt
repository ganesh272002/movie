package com.example.movieseries.ui

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.movieseries.data.PaginationState
import com.example.movieseries.data.Series
import com.example.movieseries.data.SeriesCategory
import com.example.movieseries.databinding.ItemCategoryBinding

class SeriesCategoryAdapter(
    private val fragment: Fragment,
    private val onFavoriteClick: (Series, Boolean) -> Unit,
    private val isLastPage: (String) -> Boolean,
    private val isLoading: (String) -> Boolean,
    private val onLoadMore: (String) -> Unit
) : ListAdapter<SeriesCategory, SeriesCategoryAdapter.CategoryViewHolder>(DiffCallback()) {

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

        fun bind(category: SeriesCategory) {
            binding.categoryTitle.text = category.title

            val layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.horizontalRecyclerView.layoutManager = layoutManager

            val adapter = SeriesAdapter(fragment, onFavoriteClick)
            binding.horizontalRecyclerView.adapter = adapter
            adapter.setItems(category.series)


            scrollStates[bindingAdapterPosition]?.let {
                layoutManager.onRestoreInstanceState(it)
            }

            //pagination
            binding.horizontalRecyclerView.clearOnScrollListeners()
            binding.horizontalRecyclerView.addOnScrollListener(object :
                PaginationScrollListener(layoutManager) {
                override fun isLastPage() = this@SeriesCategoryAdapter.isLastPage(category.title)
                override fun isLoading() = this@SeriesCategoryAdapter.isLoading(category.title)
                override fun loadMoreItems() = onLoadMore(category.title)
            })

            //ViewPager
            binding.horizontalRecyclerView.addOnItemTouchListener(object : OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    val action = e.getAction()
                    when (action) {
                        MotionEvent.ACTION_MOVE -> rv.getParent()
                            .requestDisallowInterceptTouchEvent(true)
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                }
            })
        }

        fun saveScrollState() {
            val lm = binding.horizontalRecyclerView.layoutManager
            scrollStates[bindingAdapterPosition] = lm?.onSaveInstanceState()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SeriesCategory>() {
        override fun areItemsTheSame(oldItem: SeriesCategory, newItem: SeriesCategory) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: SeriesCategory, newItem: SeriesCategory) =
            oldItem == newItem
    }
}
