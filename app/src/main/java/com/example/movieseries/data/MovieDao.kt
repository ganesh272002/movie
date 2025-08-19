package com.example.movieseries.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {


    @Query("SELECT * FROM saved_movies")
    fun getAllSavedMovies(): LiveData<List<MovieEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun insertMovie(movie: MovieEntity): Long


    @Delete
    suspend fun deleteMovie(movie: MovieEntity): Int


    @Query("SELECT EXISTS(SELECT * FROM saved_movies WHERE id = :id)")
    suspend fun isMovieSaved(id: Int): Boolean
}
