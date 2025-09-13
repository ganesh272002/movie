package com.example.movieseries.data


import androidx.room.Database

import androidx.room.RoomDatabase





@Database(
    entities = [SavedItemEntity::class],
    version = 3, // bump version since schema changed
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedItemDao(): SavedItemDao
}
