package com.example.movieseries.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDao {


    @Query("SELECT * FROM saved_movies")
    fun getAllSavedMovies(): LiveData<List<MovieEntity>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun insertMovie(movie: MovieEntity): Long // Return type can be Long for the row ID


    @Delete
    suspend fun deleteMovie(movie: MovieEntity): Int // Return type can be Int for the number of rows deleted


    @Query("SELECT EXISTS(SELECT * FROM saved_movies WHERE id = :id)")
    suspend fun isMovieSaved(id: Int): Boolean
}
