package com.example.movieseries.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SavedItemEntity)

    @Delete
    suspend fun deleteItem(item: SavedItemEntity)


    @Query("SELECT * FROM saved_items WHERE type = 'movie'")
    fun getAllSavedMovies(): LiveData<List<SavedItemEntity>>


    @Query("SELECT * FROM saved_items WHERE type = 'series'")
    fun getAllSavedSeries(): LiveData<List<SavedItemEntity>>


    @Query("SELECT * FROM saved_items WHERE type = :type")
    fun getItemsByType(type: String): LiveData<List<SavedItemEntity>>


    @Query("SELECT * FROM saved_items")
    fun getAllItems(): LiveData<List<SavedItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_items WHERE id = :id AND type = :type)")
    suspend fun isItemSaved(id: Int, type: String): Boolean
}
