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



    fun removeItem(item: SavedItemEntity) = viewModelScope.launch {
        repository.deleteItem(item)
    }



    val savedMovies: LiveData<List<SavedItemEntity>> = repository.getSavedMovies()
    val savedSeries: LiveData<List<SavedItemEntity>> = repository.getSavedSeries()

}
