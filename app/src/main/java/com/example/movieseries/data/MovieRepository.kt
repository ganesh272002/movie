package com.example.movieseries.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository constructor(context: Context) {

    private val dao: MovieDao = AppDatabase.getDatabase(context).movieDao()
    private val api: MovieApiService = RetrofitClient.api

    suspend fun getPopularMovies(apiKey: String, page: Int): MovieResponse = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MovieRepository", "Fetching popular movies, page=$page")
            val response = api.getPopularMovies(apiKey = apiKey, page = page)
            Log.d("MovieRepository", "Fetched ${response.results.size} movies")
            response
        } catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching movies: ${e.message}", e)
            throw e
        }
    }

    suspend fun getPopularSeries(apiKey: String, page: Int): MovieResponse = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("MovieRepository", "Fetching popular series, page=$page")
            val response = api.getPopularSeries(apiKey = apiKey, page = page)
            Log.d("MovieRepository", "Fetched ${response.results.size} series")
            response
        }
        catch (e: Exception) {
            Log.e("MovieRepository", "Error fetching series: ${e.message}", e)
            throw e
        }
    }

    fun getAllSavedMovies() = dao.getAllSavedMovies()

    suspend fun insertMovie(movie: MovieEntity): Long = withContext(Dispatchers.IO) {
        val result = dao.insertMovie(movie)

        Log.d("MovieRepository", "Inserted movie [id=${movie.id}, title=${movie.title}], rowId=$result")

        result
    }

    suspend fun deleteMovie(movie: MovieEntity): Int = withContext(Dispatchers.IO) {
        val rows = dao.deleteMovie(movie)
        Log.d("MovieRepository", "Deleted movie [id=${movie.id}, title=${movie.title}], rowsAffected=$rows")
        rows
    }

    suspend fun isMovieSaved(id: Int): Boolean = withContext(Dispatchers.IO) {
        val saved = dao.isMovieSaved(id)
        Log.d("MovieRepository", "Is movie saved? id=$id -> $saved")
        saved
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(context: Context): MovieRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MovieRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
