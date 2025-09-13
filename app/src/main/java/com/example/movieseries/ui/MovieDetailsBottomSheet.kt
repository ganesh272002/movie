package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieseries.data.Movie
import com.example.movieseries.databinding.BottomsheetMovieDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val movie = arguments?.getParcelable<Movie>("movie")
        movie?.let {
            binding.movie = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(movie: Movie): MovieDetailsBottomSheet {
            val fragment = MovieDetailsBottomSheet()
            val args = Bundle().apply {
                putParcelable("movie", movie)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
