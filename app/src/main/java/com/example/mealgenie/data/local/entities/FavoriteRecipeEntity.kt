package com.example.mealgenie.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val image: String,
    val imageType: String?
)
