package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.activityViewModels
import com.example.movieseries.data.Movie
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.BottomsheetMovieDetailsBinding
import com.example.movieseries.viewmodel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsBottomSheet(
    private val onFavoriteClick: ((Movie, Boolean) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        movie = arguments?.getParcelable("movie") ?: return
        binding.movie = movie

        binding.imageClose.setOnClickListener { dismiss() }

        // ✅ Observe DB state to update UI
        viewModel.savedMovies.observe(viewLifecycleOwner) { savedList ->
            val isSaved = savedList.any { it.id == movie.id }
            updateHeartIcon(isSaved)

            // optional: also notify adapter callback
            onFavoriteClick?.invoke(movie, isSaved)
        }

        // ✅ Toggle based on DB state
        binding.imageHeart.setOnClickListener {
            val isCurrentlySaved = viewModel.savedMovies.value?.any { it.id == movie.id } ?: false
            val newState = !isCurrentlySaved

            val entity = SavedItemEntity(
                id = movie.id,
                title = movie.title ?: "",
                name = movie.name,
                overview = movie.overview,
                posterPath = movie.posterPath,
                rating = movie.rating.toDouble(),
                type = "movie"
            )

            if (newState) viewModel.saveItem(entity) else viewModel.removeItem(entity)
        }
    }

    private fun updateHeartIcon(isSaved: Boolean) {
        binding.imageHeart.setImageResource(
            if (isSaved) com.example.movieseries.R.drawable.filled_heart
            else com.example.movieseries.R.drawable.not_filled_heart
        )
    }



    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
        bottomSheet?.let { sheet ->
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(sheet)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            behavior.isDraggable = true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            movie: Movie,
            onFavoriteClick: (Movie, Boolean) -> Unit
        ): MovieDetailsBottomSheet {
            val fragment = MovieDetailsBottomSheet(onFavoriteClick)
            val args = Bundle().apply { putParcelable("movie", movie) }
            fragment.arguments = args
            return fragment
        }
    }
}
