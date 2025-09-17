package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.data.PaginationState
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.data.Series
import com.example.movieseries.databinding.FragmentSeriesBinding
import com.example.movieseries.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var categoryAdapter: SeriesCategoryAdapter

    private val apiKey = "60af9fe8e3245c53ad9c4c0af82d56d6"

    // category title -> pagination state
    private val paginationMap = mutableMapOf<String, PaginationState>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            paginationMap.clear()
            viewModel.loadSeriesCategories(apiKey)
        }

        viewModel.loadSeriesCategories(apiKey)
    }

    private fun setupRecyclerView() {
        categoryAdapter = SeriesCategoryAdapter(
            fragment = this,
            onFavoriteClick = { series, isSaved ->
                val entity = SavedItemEntity(
                    id = series.id,
                    title = null,
                    name = series.name,
                    overview = series.overview,
                    posterPath = series.posterPath,
                    rating = series.rating.toDouble(),
                    type = "series"
                )
                if (isSaved) viewModel.saveItem(entity) else viewModel.removeItem(entity)
            },
            isLastPage = { title -> paginationMap[title]?.isLastPage ?: false },
            isLoading = { title -> paginationMap[title]?.isLoading ?: false },
            onLoadMore = { title ->
                val state = paginationMap.getOrPut(title) { PaginationState() }
                if (!state.isLoading && !state.isLastPage) {
                    state.isLoading = true
                    state.currentPage++
                    loadNextPage(title, state.currentPage)
                }
            }
        )

        binding.recyclerViewSeries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun loadNextPage(categoryTitle: String, page: Int) {
        viewModel.loadSeriesByCategory(apiKey, categoryTitle, page) { response ->
            binding.swipeRefreshLayout.isRefreshing = false
            val state = paginationMap[categoryTitle] ?: return@loadSeriesByCategory
            state.isLoading = false

            response?.let {
                state.totalPages = it.total_pages
                state.isLastPage = page >= state.totalPages

                val index = categoryAdapter.currentList.indexOfFirst { c -> c.title == categoryTitle }
                if (index != -1) {
                    val holder = binding.recyclerViewSeries.findViewHolderForAdapterPosition(index)
                            as? SeriesCategoryAdapter.CategoryViewHolder
                    holder?.let { vh ->
                        val adapter = vh.binding.horizontalRecyclerView.adapter as SeriesAdapter
                        adapter.addItems(it.results)
                    }
                }
            }
        }
    }


    private fun observeViewModel() {
        viewModel.savedSeries.observe(viewLifecycleOwner) { savedList ->
            val savedIds = savedList.map { it.id }.toSet()
            val updatedCategories = categoryAdapter.currentList.map { category ->
                category.copy(
                    series = category.series.map { it.copy(isSaved = savedIds.contains(it.id)) }
                )
            }
            categoryAdapter.submitList(updatedCategories)
        }

        viewModel.seriesCategories.observe(viewLifecycleOwner) { categories ->
            val savedIds = viewModel.savedSeries.value?.map { it.id }?.toSet() ?: emptySet()
            val updatedCategories = categories.map { category ->
                category.copy(
                    series = category.series.map { it.copy(isSaved = savedIds.contains(it.id)) }
                )
            }
            binding.swipeRefreshLayout.isRefreshing = false
            categoryAdapter.submitList(updatedCategories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
