package com.example.mealgenie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mealgenie.data.local.entities.FavoriteRecipeEntity

@Dao
interface FavoriteRecipeDao {

    @Insert
    suspend fun insertFavorite (recipe: FavoriteRecipeEntity)

    @Query ("SELECT * FROM favorite_recipes")
    suspend fun getAllFavorites():List<FavoriteRecipeEntity>

    @Query ("DELETE FROM favorite_recipes WHERE id = :recipeId")
    suspend fun removeFavorite (recipeId: Int)

    @Query ("SELECT EXISTS(SELECT * FROM favorite_recipes WHERE id = :recipeId )")
    suspend fun isFavorite(recipeId: Int): Boolean
}