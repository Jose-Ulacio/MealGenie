package com.example.mealgenie.data.repository

import android.content.Context
import androidx.room.Room
import com.example.mealgenie.data.local.database.AppDatabase
import com.example.mealgenie.data.local.entities.FavoriteRecipeEntity
import com.example.mealgenie.data.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "favorites_db"
    ).build()

    private val dao = db.favoriteRecipeDao()

    //Añade una receta a favoritos: Convierte el objeto Recipe a FavoriteRecipeEntity (usando la función de extensión)
    suspend fun addFavorite(recipe: Recipe){
        dao.insertFavorite(recipe.toFavoriteEntity())
    }

    //Elimina una receta de favoritos por su ID
    suspend fun removeFavorite(recipeId: Int){
        dao.removeFavorite(recipeId)
    }

    //Obtiene todas las recetas favoritas: Obtiene las entidades de la base de datos,
    //Convierte cada entidad a un objeto Recipe (usando la función de extensión) y Retorna la lista convertida
//    suspend fun getFavorites():List<Recipe>{
//        return dao.getAllFavorites().map { it.toRecipe() }
//    }
    fun getFavorites(): Flow<List<Recipe>> = flow {
        try {
            val favorites = dao.getAllFavorites().map { it.toRecipe() }
            emit(favorites)
        } catch (e: Exception){
            //Manejo de Errores
        }
    }

    //Verifica si una receta específica está marcada como favorita
    suspend fun isFavorite(recipeId: Int): Boolean {
        return dao.isFavorite(recipeId)
    }

    /*Funciones de Extension que funcionaran como conversores*/

    //Convierte Recipe(API) a entidad(DB)
    private fun Recipe.toFavoriteEntity(): FavoriteRecipeEntity{
        return FavoriteRecipeEntity(
            id = this.id,
            title = this.title,
            image = this.image,
            imageType = this.imageType
        )
    }

    //Convierte entidad (DB) a Recipe(API)
    private fun FavoriteRecipeEntity.toRecipe(): Recipe{
        return Recipe(
            id = this.id,
            title = this.title,
            image = this.image,
            imageType = this.imageType
        )
    }
}