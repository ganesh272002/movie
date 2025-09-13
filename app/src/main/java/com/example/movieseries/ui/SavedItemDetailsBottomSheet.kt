package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.BottomsheetSavedItemDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SavedItemDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSavedItemDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSavedItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = arguments?.getParcelable<SavedItemEntity>(ARG_ITEM)
        item?.let { binding.item2 = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM = "item"

        fun newInstance(item: SavedItemEntity): SavedItemDetailsBottomSheet {
            return SavedItemDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ITEM, item)
                }
            }
        }
    }
}
