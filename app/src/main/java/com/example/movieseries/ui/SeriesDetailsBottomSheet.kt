package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieseries.data.Series
import com.example.movieseries.databinding.BottomsheetSeriesDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SeriesDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSeriesDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSeriesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val series = arguments?.getParcelable<Series>("series")
        series?.let {
            binding.series = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(series: Series): SeriesDetailsBottomSheet {
            val fragment = SeriesDetailsBottomSheet()
            val args = Bundle().apply {
                putParcelable("series", series)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
