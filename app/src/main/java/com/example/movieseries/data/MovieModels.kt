package com.example.movieseries.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class Movie(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("overview") val overview: String
)


data class MovieResponse(
    val results: List<Movie>
)


data class Category(
    val title: String,
    val movies: List<Movie>
)
@Entity(tableName = "saved_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String,
    val rating: Float
)
