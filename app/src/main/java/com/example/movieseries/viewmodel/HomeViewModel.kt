package com.example.movieseries.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieseries.data.Movie
import com.example.movieseries.data.MovieEntity
import com.example.movieseries.data.MovieRepository
import com.example.movieseries.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _popularMovies = MutableLiveData<Resource<List<Movie>>>()
    val popularMovies: LiveData<Resource<List<Movie>>> = _popularMovies

    private val _popularSeries = MutableLiveData<List<Movie>>()
    val popularSeries: LiveData<List<Movie>> get() = _popularSeries

    val searchText = MutableLiveData<String>() // For two-way binding example

    fun loadPopularMovies(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            _popularMovies.postValue(Resource.Loading())

            try {
                val response = repository.getPopularMovies(apiKey, page)
                _popularMovies.postValue(Resource.Success(response.results))
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> "HTTP ${e.code()} ${e.message()}"
                    is IOException -> "Network error. Check your connection."
                    else -> e.localizedMessage ?: "Unknown error occurred"
                }
                _popularMovies.postValue(Resource.Error(errorMessage))
            }
        }
    }

    fun loadPopularSeries(apiKey: String, page: Int = 1) {
        viewModelScope.launch {
            try {
                val response = repository.getPopularSeries(apiKey, page)
                _popularSeries.postValue(response.results)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

   // fun getSavedMovies() = repository.getAllSavedMovies()

    fun saveMovie(movie: MovieEntity) = viewModelScope.launch {
        repository.insertMovie(movie)
    }

    fun deleteMovie(movie: MovieEntity) = viewModelScope.launch {
        repository.deleteMovie(movie)
    }

    suspend fun isMovieSaved(id: Int): Boolean = withContext(Dispatchers.IO) {
        repository.isMovieSaved(id)
    }
}