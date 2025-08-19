package com.example.movieseries.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.movieseries.data.MovieEntity
import com.example.movieseries.data.MovieRepository

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(application)

    fun getSavedMovies(): LiveData<List<MovieEntity>> {
        return repository.getAllSavedMovies()
    }
}