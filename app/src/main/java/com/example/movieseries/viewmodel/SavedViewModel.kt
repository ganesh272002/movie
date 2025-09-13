package com.example.movieseries.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieseries.data.SavedItemEntity
import com.example.movieseries.data.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    // Observe all saved items (movies + series)
    fun getSavedItems(): LiveData<List<SavedItemEntity>> =
        repository.getItemsByType("%") // Or better: repository.getAllItems() if you expose it

    // Optional: still keep type-specific fetch if needed
    fun getItemsByType(type: String): LiveData<List<SavedItemEntity>> =
        repository.getItemsByType(type)

    fun saveItem(item: SavedItemEntity) = viewModelScope.launch {
        repository.insertItem(item)
    }

    fun removeItem(item: SavedItemEntity) = viewModelScope.launch {
        repository.deleteItem(item)
    }

    suspend fun isItemSaved(id: Int, type: String): Boolean =
        repository.isItemSaved(id, type)

    val savedMovies: LiveData<List<SavedItemEntity>> = repository.getSavedMovies()
    val savedSeries: LiveData<List<SavedItemEntity>> = repository.getSavedSeries()

}
