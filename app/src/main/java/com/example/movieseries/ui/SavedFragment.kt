package com.example.movieseries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.databinding.FragmentSavedBinding
import com.example.movieseries.viewmodel.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedMoviesAdapter: SavedItemsAdapter
    private lateinit var savedSeriesAdapter: SavedItemsAdapter
    private val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMoviesRecycler()
        setupSeriesRecycler()
        observeSavedItems()
    }

    private fun setupMoviesRecycler() {
        savedMoviesAdapter = SavedItemsAdapter(
            onRemoveClick = { item -> removeFromSaved(item) },
            onItemLongClick = { item -> showItemDetails(item) } // ✅ long press opens bottom sheet
        )

        binding.recyclerViewSaved.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = savedMoviesAdapter
        }
    }

    private fun setupSeriesRecycler() {
        savedSeriesAdapter = SavedItemsAdapter(
            onRemoveClick = { item -> removeFromSaved(item) },
            onItemLongClick = { item -> showItemDetails(item) } // ✅ long press opens bottom sheet
        )

        binding.recyclerViewSavedSeries.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = savedSeriesAdapter
        }
    }

    private fun observeSavedItems() {viewModel.savedMovies.observe(viewLifecycleOwner) { movies ->
        savedMoviesAdapter.submitList(movies)
        binding.emptyView.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
    }

        viewModel.savedSeries.observe(viewLifecycleOwner) { series ->
            savedSeriesAdapter.submitList(series)
            binding.emptySeriesView.visibility = if (series.isEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun removeFromSaved(item: SavedItemEntity) {
        lifecycleScope.launch {
            viewModel.removeItem(item)
        }
    }

    private fun showItemDetails(item: SavedItemEntity) {
        val bottomSheet = SavedItemDetailsBottomSheet.newInstance(item)
        bottomSheet.show(parentFragmentManager, "SavedItemDetails")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
