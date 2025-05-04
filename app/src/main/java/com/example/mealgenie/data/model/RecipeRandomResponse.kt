package com.example.mealgenie.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeRandomResponse(
    @SerialName("recipes") val recipes: List<Recipe>
)
