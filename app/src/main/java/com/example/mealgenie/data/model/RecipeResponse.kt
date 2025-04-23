package com.example.mealgenie.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    @SerialName("results") val results: List<Recipe>,
    @SerialName("offset") val offset: Int,
    @SerialName("number") val number: Int,
    @SerialName("totalResults") val totalResults: Int
)

@Serializable
data class Recipe(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("image") val image: String,
    @SerialName("imageType") val imageType: String? = null
)
