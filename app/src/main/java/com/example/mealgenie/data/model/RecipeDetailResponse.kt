package com.example.mealgenie.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailResponse(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("image") val image: String,
    @SerialName("summary") val summary: String,
    @SerialName("extendedIngredients") val extendedIngredients: List<Ingredient>,
    @SerialName("analyzedInstructions") val analyzedInstructions: List<Instruction>,
    @SerialName("readyInMinutes") val readyInMinutes: Int,
    @SerialName("servings") val servings: Int
)

@Serializable
data class Ingredient(
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Double,
    @SerialName("unit") val unit: String
)

@Serializable
data class Instruction(
    @SerialName("steps") val steps: List<Step>
)

@Serializable
data class Step(
    @SerialName("number") val number: Int,
    @SerialName("step") val text: String
)
