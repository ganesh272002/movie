package com.example.movieseries.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.R
import com.example.movieseries.data.MovieEntity
import com.example.movieseries.data.MovieRepository
import com.example.movieseries.data.Resource
import com.example.movieseries.databinding.FragmentHomeBinding
import com.example.movieseries.databinding.FragmentMovieBinding
import com.example.movieseries.viewmodel.HomeViewModel
import com.example.movieseries.viewmodel.HomeViewModelFactory


class MovieFragment : Fragment() {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var movieAdapter: MoviesAdapter
    private lateinit var seriesAdapter: MoviesAdapter

    private val apiKey = "60af9fe8e3245c53ad9c4c0af82d56d6"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)

        // ViewModel scoped to Fragment (or use activityViewModels if shared)
        val repository = MovieRepository.getInstance(requireContext())
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerViews()
        observeViewModel()


        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPopularMovies(apiKey)

        }


        viewModel.loadPopularMovies(apiKey)

    }

    private fun setupRecyclerViews() {

        movieAdapter = MoviesAdapter { movie ->
            val entity = movie.toEntity()
            viewModel.saveMovie(entity)
        }


        seriesAdapter = MoviesAdapter { movie ->
            val entity = movie.toEntity()
            viewModel.saveMovie(entity)
        }

        binding.recyclerViewMovies.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = movieAdapter
        }


    }

    private fun observeViewModel() {
        viewModel.popularMovies.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }

                is Resource.Success -> {
                    movieAdapter.submitList(resource.data)
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun com.example.movieseries.data.Movie.toEntity(): MovieEntity {
        return MovieEntity(
            id = id,
            title = title ?: name.orEmpty(),
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            rating = rating
        )
    }
}

