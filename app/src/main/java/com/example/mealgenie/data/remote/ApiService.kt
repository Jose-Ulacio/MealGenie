package com.example.mealgenie.data.remote

import android.util.Log
import com.example.mealgenie.data.model.RecipeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay

class ApiService() {
    private val apiClient = KtorClient.apiClient()

    private companion object {
        const val BASE_URL = "https://api.spoonacular.com"
        const val API_KEY = "7ed15c894d7d477ca48b0b856c161689"
    }

    suspend fun getRecipes(
        type: String?,
        offset: Int,
        number: Int
    ): Result<RecipeResponse> {
        return try {
            val response = apiClient.get("$BASE_URL/recipes/complexSearch"){
                parameter("apiKey", API_KEY)
                parameter("offset", offset)
                parameter("number", number)
                parameter("sort", "random") //Para Obtener Recetas Aleatorias
                type?.let { parameter("type", it) }

            }
            if (response.status.isSuccess()){
                Result.success(response.body())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e: Exception){
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun searchRecipes(
        query: String?,
        number: Int
    ): RecipeResponse? {
        return apiClient.get("$BASE_URL/recipes/complexSearch"){
            parameter("apiKey", API_KEY)
            parameter("query", query)
            parameter("number", number)
        }.body()
    }


}