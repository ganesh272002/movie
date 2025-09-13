package com.example.movieseries.di

import com.example.movieseries.data.MovieApiService
import com.example.movieseries.data.SavedItemDao
import com.example.movieseries.data.SavedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSavedRepository(
        savedDao: SavedItemDao,
        api: MovieApiService
    ): SavedRepository = SavedRepository(savedDao, api)
}
