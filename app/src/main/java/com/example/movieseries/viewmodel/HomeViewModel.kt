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

    // ------------------ Movie categories ------------------
    private val _movieCategories = MutableLiveData<List<MovieCategory>>()
    val movieCategories: LiveData<List<MovieCategory>> = _movieCategories

    private val _seriesCategories = MutableLiveData<List<SeriesCategory>>()
    val seriesCategories: LiveData<List<SeriesCategory>> = _seriesCategories

    fun loadMovieCategories(apiKey: String) {
        viewModelScope.launch {
            val list = mutableListOf<MovieCategory>()

            repository.getPopularMovies(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(MovieCategory("Popular Movies", (it as Resource.Success).data.results))
            }
            repository.getTopRatedMovies(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(MovieCategory("Top Rated Movies", (it as Resource.Success).data.results))
            }
            repository.getUpcomingMovies(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(MovieCategory("Upcoming Movies", (it as Resource.Success).data.results))
            }
            repository.getTrendingMovies(apiKey).takeIf { it is Resource.Success }?.let {
                list.add(MovieCategory("Trending Movies", (it as Resource.Success).data.results))
            }

            _movieCategories.postValue(list)
        }
    }

    fun loadSeriesCategories(apiKey: String) {
        viewModelScope.launch {
            val list = mutableListOf<SeriesCategory>()

            repository.getPopularSeries(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(SeriesCategory("Popular Series", (it as Resource.Success).data.results))
            }
            repository.getTopRatedSeries(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(SeriesCategory("Top Rated Series", (it as Resource.Success).data.results))
            }
            repository.getUpcomingSeries(apiKey, 1).takeIf { it is Resource.Success }?.let {
                list.add(SeriesCategory("Upcoming Series", (it as Resource.Success).data.results))
            }
            repository.getTrendingSeries(apiKey).takeIf { it is Resource.Success }?.let {
                list.add(SeriesCategory("Trending Series", (it as Resource.Success).data.results))
            }

            _seriesCategories.postValue(list)
        }
    }

    fun loadMoviesByCategory(
        apiKey: String,
        category: String,
        page: Int,
        callback: (MovieResponse?) -> Unit
    ) {
        viewModelScope.launch {
            val response = when (category) {
                "Popular Movies" -> repository.getPopularMovies(apiKey, page)
                "Top Rated Movies" -> repository.getTopRatedMovies(apiKey, page)
                "Upcoming Movies" -> repository.getUpcomingMovies(apiKey, page)
                "Trending Movies" -> repository.getTrendingMovies(apiKey)
                else -> null
            }
            if (response is Resource.Success) {
                callback(response.data)
            } else {
                callback(null)
            }
        }
    }

    fun loadSeriesByCategory(
        apiKey: String,
        category: String,
        page: Int,
        callback: (SeriesResponse?) -> Unit
    ) {
        viewModelScope.launch {
            val response = when (category) {
                "Popular Series" -> repository.getPopularSeries(apiKey, page)
                "Top Rated Series" -> repository.getTopRatedSeries(apiKey, page)
                "Upcoming Series" -> repository.getUpcomingSeries(apiKey, page)
                "Trending Series" -> repository.getTrendingSeries(apiKey)
                else -> null
            }
            if (response is Resource.Success) callback(response.data) else callback(null)
        }
    }

    // ------------------ Search ------------------
    private val _searchResultsMovies = MutableLiveData<List<Movie>>()
    val searchResultsMovies: LiveData<List<Movie>> = _searchResultsMovies

    fun searchMoviesLocally(query: String) {
        if (query.isBlank()) {
            _searchResultsMovies.postValue(emptyList())
            return
        }

        val allMovies = _movieCategories.value?.flatMap { it.movies } ?: emptyList()
        val filtered = allMovies.filter { it.title?.contains(query, ignoreCase = true) == true }
        _searchResultsMovies.postValue(filtered)
    }
}
