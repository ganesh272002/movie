package com.example.movieseries.data

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SavedRepository @Inject constructor(
    private val dao: SavedItemDao,
    private val api: MovieApiService
) {
    // ---- Local (Database) ----
    fun getItemsByType(type: String) = dao.getItemsByType(type)
    fun getSavedMovies() = dao.getAllSavedMovies()
    fun getSavedSeries() = dao.getAllSavedSeries()

    suspend fun insertItem(item: SavedItemEntity) = dao.insertItem(item)
    suspend fun deleteItem(item: SavedItemEntity) = dao.deleteItem(item)
    suspend fun isItemSaved(id: Int, type: String) = dao.isItemSaved(id, type)

    // ---- Remote (Movies) ----
    suspend fun getPopularMovies(apiKey: String, page: Int): Resource<MovieResponse> {
        return try {
            val response = api.getPopularMovies(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getTopRatedMovies(apiKey: String, page: Int): Resource<MovieResponse> {
        return try {
            val response = api.getTopRatedMovies(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getUpcomingMovies(apiKey: String, page: Int): Resource<MovieResponse> {
        return try {
            val response = api.getUpcomingMovies(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getTrendingMovies(apiKey: String): Resource<MovieResponse> {
        return try {
            val response = api.getTrendingMovies(apiKey = apiKey)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    // ---- Remote (Series) ----
    suspend fun getPopularSeries(apiKey: String, page: Int): Resource<SeriesResponse> {
        return try {
            val response = api.getPopularSeries(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getTopRatedSeries(apiKey: String, page: Int): Resource<SeriesResponse> {
        return try {
            val response = api.getTopRatedSeries(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getUpcomingSeries(apiKey: String, page: Int): Resource<SeriesResponse> {
        return try {
            val response = api.getUpcomingSeries(apiKey = apiKey, page = page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    suspend fun getTrendingSeries(apiKey: String): Resource<SeriesResponse> {
        return try {
            val response = api.getTrendingSeries(apiKey = apiKey)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }




}
