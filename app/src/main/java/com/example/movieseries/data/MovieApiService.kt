package com.example.movieseries.data

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {


    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): MovieResponse


    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): MovieResponse
}
