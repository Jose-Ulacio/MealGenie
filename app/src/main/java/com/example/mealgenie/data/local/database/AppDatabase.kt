package com.example.mealgenie.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mealgenie.data.local.dao.FavoriteRecipeDao
import com.example.mealgenie.data.local.entities.FavoriteRecipeEntity

@Database(
    entities = [FavoriteRecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}