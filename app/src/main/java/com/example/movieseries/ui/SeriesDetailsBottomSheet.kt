package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.data.Series
import com.example.movieseries.databinding.BottomsheetSeriesDetailsBinding
import com.example.movieseries.viewmodel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SeriesDetailsBottomSheet(
    private val onFavoriteClick: ((Series, Boolean) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSeriesDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var series: Series

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSeriesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        series = arguments?.getParcelable("series") ?: return
        binding.series = series

        binding.imageClose.setOnClickListener { dismiss() }

        // Observe DB state to update heart icon
        viewModel.savedSeries.observe(viewLifecycleOwner) { savedList ->
            val isSaved = savedList.any { it.id == series.id }
            updateHeartIcon(isSaved)

            // Optional: notify adapter callback
            onFavoriteClick?.invoke(series, isSaved)
        }

        // Toggle favorite
        binding.imageHeart.setOnClickListener {
            val isCurrentlySaved = viewModel.savedSeries.value?.any { it.id == series.id } ?: false
            val newState = !isCurrentlySaved

            val entity = SavedItemEntity(
                id = series.id,
                title = null,
                name = series.name,
                overview = series.overview,
                posterPath = series.posterPath,
                rating = series.rating.toDouble(),
                type = "series"
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
            series: Series,
            onFavoriteClick: (Series, Boolean) -> Unit
        ): SeriesDetailsBottomSheet {
            val fragment = SeriesDetailsBottomSheet(onFavoriteClick)
            val args = Bundle().apply { putParcelable("series", series) }
            fragment.arguments = args
            return fragment
        }
    }
}
