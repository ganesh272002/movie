package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.data.PaginationState
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.FragmentMovieBinding
import com.example.movieseries.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: MoviesAdapter
    private lateinit var categoryAdapter: MovieCategoryAdapter

    private val viewModel: HomeViewModel by activityViewModels()
    private val apiKey = "60af9fe8e3245c53ad9c4c0af82d56d6"

    private val paginationMap = mutableMapOf<String, PaginationState>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentMovieBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        observeViewModel()
        binding.swipeRefreshLayout.setOnRefreshListener {
            paginationMap.clear()
            viewModel.loadMovieCategories(apiKey)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        viewModel.loadMovieCategories(apiKey)
    }

    private fun setupRecyclerView() {
        categoryAdapter = MovieCategoryAdapter(
            fragment = this,
            viewModel = viewModel,
            onFavoriteClick = { movie, isSaved ->
                val entity = SavedItemEntity(
                    id = movie.id,
                    title = movie.title ?: "",
                    name = null,
                    overview = movie.overview,
                    posterPath = movie.posterPath,
                    rating = movie.rating.toDouble(),
                    type = "movie"
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

        searchAdapter = MoviesAdapter(this) { movie, isSaved ->
            val entity = SavedItemEntity(
                id = movie.id,
                title = movie.title ?: "",
                name = null,
                overview = movie.overview,
                posterPath = movie.posterPath,
                rating = movie.rating.toDouble(),
                type = "movie"
            )
            if (isSaved) viewModel.saveItem(entity) else viewModel.removeItem(entity)
        }

        binding.recyclerViewMovies.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = categoryAdapter
        }

        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = searchAdapter
        }
    }

    private fun loadNextPage(categoryTitle: String, page: Int) {
        viewModel.loadMoviesByCategory(apiKey, categoryTitle, page) { response ->
            val state = paginationMap[categoryTitle] ?: return@loadMoviesByCategory
            state.isLoading = false
            response?.let {
                state.totalPages = it.total_pages
                state.isLastPage = page >= state.totalPages
                val index = categoryAdapter.currentList.indexOfFirst { c -> c.title == categoryTitle }
                if (index != -1) {
                    val holder = binding.recyclerViewMovies.findViewHolderForAdapterPosition(index)
                            as? MovieCategoryAdapter.CategoryViewHolder
                    holder?.binding?.horizontalRecyclerView?.adapter?.let { adapter ->
                        (adapter as MoviesAdapter).submitList((adapter as MoviesAdapter).currentList + it.results)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.savedMovies.observe(viewLifecycleOwner) { savedList ->
            val savedIds = savedList.map { it.id }.toSet()
            categoryAdapter.updateSavedMovies(savedIds)
            val updatedSearch = searchAdapter.currentList.map { it.copy(isSaved = savedIds.contains(it.id)) }
            searchAdapter.submitList(updatedSearch)
        }

        viewModel.movieCategories.observe(viewLifecycleOwner) { categories ->
            val savedIds = viewModel.savedMovies.value?.map { it.id }?.toSet() ?: emptySet()
            val updatedCategories = categories.map { category ->
                category.copy(movies = category.movies.map { it.copy(isSaved = savedIds.contains(it.id)) })
            }
            categoryAdapter.submitList(updatedCategories)
        }

        viewModel.searchResultsMovies.observe(viewLifecycleOwner) { results ->
            val savedIds = viewModel.savedMovies.value?.map { it.id }?.toSet() ?: emptySet()
            val uniqueResults = results.distinctBy { it.id }.map { it.copy(isSaved = savedIds.contains(it.id)) }

            if (uniqueResults.isNotEmpty()) {
                binding.recyclerViewSearchResults.visibility = View.VISIBLE
                binding.recyclerViewMovies.visibility = View.GONE
                searchAdapter.submitList(uniqueResults)
            } else {
                binding.recyclerViewSearchResults.visibility = View.GONE
                binding.recyclerViewMovies.visibility = View.VISIBLE
            }
        }
    }

    fun hideSearchResults() {
        binding.recyclerViewSearchResults.visibility = View.GONE
        binding.recyclerViewMovies.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
