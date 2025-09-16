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
import com.example.movieseries.databinding.FragmentMovieBinding
import com.example.movieseries.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment() {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var categoryAdapter: MovieCategoryAdapter

    private val apiKey = "60af9fe8e3245c53ad9c4c0af82d56d6"

    // Track pagination state per category (title -> PaginationState)
    private val paginationMap = mutableMapOf<String, PaginationState>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        observeViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            paginationMap.clear()
            viewModel.loadMovieCategories(apiKey)
        }

        // initial load
        viewModel.loadMovieCategories(apiKey)
    }

    private fun setupRecyclerView() {
        categoryAdapter = MovieCategoryAdapter(
            fragment = this,
            onFavoriteClick = { movie, isSaved ->
                val entity = SavedItemEntity(
                    id = movie.id,
                    title = movie.title ?: movie.name.orEmpty(),
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

        binding.recyclerViewMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun loadNextPage(categoryTitle: String, page: Int) {
        viewModel.loadMoviesByCategory(apiKey, categoryTitle, page) { response ->
            binding.swipeRefreshLayout.isRefreshing = false
            val state = paginationMap[categoryTitle] ?: return@loadMoviesByCategory
            state.isLoading = false

            response?.let {
                state.totalPages = it.total_pages
                state.isLastPage = page >= state.totalPages

                // Find the row by category title
                val index = categoryAdapter.currentList.indexOfFirst { c -> c.title == categoryTitle }
                if (index != -1) {
                    val holder = binding.recyclerViewMovies.findViewHolderForAdapterPosition(index)
                            as? MovieCategoryAdapter.CategoryViewHolder
                    holder?.let { vh ->
                        val movieAdapter = vh.binding.horizontalRecyclerView.adapter as MoviesAdapter
                        movieAdapter.addItems(it.results) // ✅ append items
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe DB saved items
        viewModel.savedMovies.observe(viewLifecycleOwner) { savedList ->
            val savedIds = savedList.map { it.id }.toSet()
            val updatedCategories = categoryAdapter.currentList.map { category ->
                category.copy(
                    movies = category.movies.map { it.copy(isSaved = savedIds.contains(it.id)) }
                )
            }
            categoryAdapter.submitList(updatedCategories)
        }

        // Observe API categories
        viewModel.movieCategories.observe(viewLifecycleOwner) { categories ->
            val savedIds = viewModel.savedMovies.value?.map { it.id }?.toSet() ?: emptySet()
            val updatedCategories = categories.map { category ->
                category.copy(
                    movies = category.movies.map { it.copy(isSaved = savedIds.contains(it.id)) }
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
