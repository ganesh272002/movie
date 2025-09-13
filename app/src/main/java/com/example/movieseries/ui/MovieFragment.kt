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
            currentPage = 1
            isLastPage = false
            viewModel.loadMovieCategories(apiKey)
        }

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
            onLoadMore = { // horizontal pagination callback
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

    private fun loadNextPage() {
        viewModel.loadPopularMovies(apiKey, currentPage) { response ->
            binding.swipeRefreshLayout.isRefreshing = false
            isLoading = false

            response?.let {
                totalPages = it.total_pages

                // Append new items to "Popular Movies" category
                val currentList = categoryAdapter.currentList.toMutableList()
                val index = currentList.indexOfFirst { c -> c.title == "Popular Movies" }
                if (index != -1) {
                    val updatedCategory = currentList[index].copy(
                        movies = currentList[index].movies + it.results
                    )
                    currentList[index] = updatedCategory
                    categoryAdapter.submitList(currentList)
                }
            }
            isLastPage = currentPage >= totalPages
        }
    }

    private fun observeViewModel() {
        viewModel.savedMovies.observe(viewLifecycleOwner) { savedList ->
            val savedIds = savedList.map { it.id }.toSet()
            val updatedCategories = categoryAdapter.currentList.map { category ->
                category.copy(
                    movies = category.movies.map { it.copy(isSaved = savedIds.contains(it.id)) }
                )
            }
            categoryAdapter.submitList(updatedCategories)
        }

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
