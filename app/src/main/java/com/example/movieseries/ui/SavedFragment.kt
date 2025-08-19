package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.data.AppDatabase
import com.example.movieseries.data.MovieDao
import com.example.movieseries.data.MovieEntity
import com.example.movieseries.data.MovieRepository
import com.example.movieseries.databinding.FragmentSavedBinding
import kotlinx.coroutines.launch

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedMoviesAdapter: SavedMoviesAdapter
    private lateinit var movieDao: MovieDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DAO instance
        movieDao = AppDatabase.getDatabase(requireContext()).movieDao()

        // Adapter
        savedMoviesAdapter = SavedMoviesAdapter { movie ->
            removeFromSaved(movie)
        }

        // Grid Layout Manager for 2 columns
        binding.recyclerViewSaved.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSaved.adapter = savedMoviesAdapter

        loadSavedMovies()
    }

    private fun loadSavedMovies() {
        movieDao.getAllSavedMovies().observe(viewLifecycleOwner) { movies ->
            savedMoviesAdapter.submitList(movies)
            binding.emptyView.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
        }
    }




    private fun removeFromSaved(movie: MovieEntity) {
        lifecycleScope.launch {
            movieDao.deleteMovie(movie)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

