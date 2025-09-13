package com.example.movieseries.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieseries.data.*
import com.example.movieseries.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    // ------------------ Local Database ------------------
    val savedMovies: LiveData<List<SavedItemEntity>> = repository.getItemsByType("movie")
    val savedSeries: LiveData<List<SavedItemEntity>> = repository.getItemsByType("series")

    fun saveItem(item: SavedItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(item)
    }

    fun removeItem(item: SavedItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(item)
    }

    suspend fun isItemSaved(id: Int, type: String): Boolean {
        return repository.isItemSaved(id, type)
    }

    // ------------------ Remote API Categories ------------------

    private val _movieCategories = MutableLiveData<List<MovieCategory>>()
    val movieCategories: LiveData<List<MovieCategory>> = _movieCategories

    private val _seriesCategories = MutableLiveData<List<SeriesCategory>>()
    val seriesCategories: LiveData<List<SeriesCategory>> = _seriesCategories

    // ------------------ Loaders ------------------

    fun loadMovieCategories(apiKey: String) {
        viewModelScope.launch {
            val movieCategoryList = mutableListOf<MovieCategory>()

            val popular = repository.getPopularMovies(apiKey, 1)
            if (popular is Resource.Success) {
                movieCategoryList.add(MovieCategory("Popular Movies", popular.data.results))
            }

            val topRated = repository.getTopRatedMovies(apiKey, 1)
            if (topRated is Resource.Success) {
                movieCategoryList.add(MovieCategory("Top Rated Movies", topRated.data))
            }

            val upcoming = repository.getUpcomingMovies(apiKey, 1)
            if (upcoming is Resource.Success) {
                movieCategoryList.add(MovieCategory("Upcoming Movies", upcoming.data))
            }

            val trending = repository.getTrendingMovies(apiKey)
            if (trending is Resource.Success) {
                movieCategoryList.add(MovieCategory("Trending Movies", trending.data))
            }

            _movieCategories.postValue(movieCategoryList)
        }
    }

    fun loadSeriesCategories(apiKey: String) {
        viewModelScope.launch {
            val seriesCategoryList = mutableListOf<SeriesCategory>()

            val popular = repository.getPopularSeries(apiKey, 1)
            if (popular is Resource.Success) {
                seriesCategoryList.add(SeriesCategory("Popular Series", popular.data))
            }

            val topRated = repository.getTopRatedSeries(apiKey, 1)
            if (topRated is Resource.Success) {
                seriesCategoryList.add(SeriesCategory("Top Rated Series", topRated.data))
            }

            val upcoming = repository.getUpcomingSeries(apiKey, 1)
            if (upcoming is Resource.Success) {
                seriesCategoryList.add(SeriesCategory("Upcoming Series", upcoming.data))
            }

            val trending = repository.getTrendingSeries(apiKey)
            if (trending is Resource.Success) {
                seriesCategoryList.add(SeriesCategory("Trending Series", trending.data))
            }

            _seriesCategories.postValue(seriesCategoryList)
        }
    }

    // ------------------ Pagination: Popular Movies ------------------
    fun loadPopularMovies(
        apiKey: String,
        page: Int,
        callback: (MovieResponse?) -> Unit
    ) {
        viewModelScope.launch {
            val response = repository.getPopularMovies(apiKey, page)
            if (response is Resource.Success) {
                callback(response.data) // <-- contains results, page, total_pages
            } else {
                callback(null)
            }
        }
    }
}



