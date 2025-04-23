package com.example.mealgenie.data.repository

import com.example.mealgenie.data.model.RecipeResponse
import com.example.mealgenie.data.remote.ApiService

class RecipeRepository() {
    val apiService: ApiService = ApiService()

    //Obtener Lista de Recetas
    suspend fun getRecipes(
        type: String?,
        offset: Int,
        number: Int
    ): RecipeResponse? {
        return apiService.getRecipes(
            type = type,
            offset = offset,
            number = number
        ).getOrNull()
    }
}