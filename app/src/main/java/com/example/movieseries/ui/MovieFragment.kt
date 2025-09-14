package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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

    // Pagination state (only for Popular Movies)
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false
    private var isLastPage = false

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
            resetPagination()
            viewModel.loadMovieCategories(apiKey)
        }

        // first load
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
                if (isSaved) {
                    viewModel.saveItem(entity)
                } else {
                    viewModel.removeItem(entity)
                }
            },
            isLastPage = { isLastPage },   // delegate to fragment’s state
            isLoading = { isLoading },
            // horizontal scroll pagination callback for "Popular Movies"
            onLoadMore = {
                if (!isLoading && !isLastPage) {
                    isLoading = true
                    currentPage++
                    loadNextPage()
                }
            }
        )

        binding.recyclerViewMovies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun resetPagination() {
        currentPage = 1
        totalPages = 1
        isLoading = false
        isLastPage = false
    }

    private fun loadNextPage() {
        viewModel.loadPopularMovies(apiKey, currentPage) { response ->
            binding.swipeRefreshLayout.isRefreshing = false
            isLoading = false

            response?.let {
                totalPages = it.total_pages
                isLastPage = currentPage >= totalPages

                // Find Popular Movies row
                val index = categoryAdapter.currentList.indexOfFirst { c -> c.title == "Popular Movies" }
                if (index != -1) {
                    // Get that row's ViewHolder
                    val holder = binding.recyclerViewMovies.findViewHolderForAdapterPosition(index)
                            as? MovieCategoryAdapter.CategoryViewHolder

                    holder?.let { vh ->
                        val movieAdapter = vh.binding.horizontalRecyclerView.adapter as MoviesAdapter
                        movieAdapter.addItems(it.results)  // ✅ append items without reset
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
