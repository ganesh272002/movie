package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.movieseries.R
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.BottomsheetSavedItemDetailsBinding
import com.example.movieseries.viewmodel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SavedItemDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetSavedItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSavedItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageClose.setOnClickListener { dismiss() }

        val item = arguments?.getParcelable<SavedItemEntity>(ARG_ITEM)

        item?.let { savedItem ->
            binding.item2 = savedItem

            binding.imageHeart.setOnClickListener {

                viewModel.removeItem(savedItem)


                binding.imageHeart.setImageResource(R.drawable.not_filled_heart)


            }
        }




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
