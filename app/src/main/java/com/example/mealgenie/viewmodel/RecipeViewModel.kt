package com.example.mealgenie.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealgenie.R
import com.example.mealgenie.data.model.Recipe
import com.example.mealgenie.data.model.RecipeDetailResponse
import com.example.mealgenie.data.repository.FavoriteRepository
import com.example.mealgenie.data.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RecipeViewModel(context: Context): ViewModel() {
    private val appContext = context.applicationContext

    private val repository = RecipeRepository()
    private val favoriteRepository = FavoriteRepository(context)

    //Estados
    //Recetas
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    //Cargando
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    //Cargando Mas
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    //Error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    //Type "Chip" seleccionado
    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10     //Numero de recetas que trae el servidor

    //Refrescar
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    //Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults

    private val _recipeDetail = MutableStateFlow<RecipeDetailResponse?>(null)
    val recipeDetail: StateFlow<RecipeDetailResponse?> = _recipeDetail.asStateFlow()

    //recetas aleatorias
    private val _randomRecipe = MutableStateFlow<Recipe?>(null)
    val randomRecipe: StateFlow<Recipe?> = _randomRecipe.asStateFlow()

    init {
        loadRecipes()
    }

    fun refreshRecipe(){
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                currentPage = 0
                val newRecipes = repository.getRecipes(
                    type = _selectedType.value,
                    offset = 0, //Para que siempre cargue desde el inicio
                    number = pageSize
                )
                newRecipes?.results.let {
                    if (it != null) {
                        _recipes.value = it //Reemplaza la lista
                    }
                    currentPage = 1
                }
            } catch (e: Exception){
                _error.value = appContext.getString(R.string.refresh_error)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getRecipes(
                    type = _selectedType.value,        //el chip seleccionado
                    offset = 0,
                    number = pageSize
                )
                result?.results?.let { newRecipes ->
                    _recipes.value = newRecipes
                    currentPage = 1
                }
            } catch (e: Exception){
                _error.value = e.message ?: appContext.getString(R.string.unknown_error)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreRecipes(){
        if (_isLoadingMore.value || _isLoading.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            delay(2000)
            try {
                val result = repository.getRecipes(
                    type = _selectedType.value,
                    offset = currentPage * pageSize,
                    number = pageSize
                )
                result?.results?.let {newRecipes ->
                    _recipes.value = _recipes.value + newRecipes
                    currentPage++
                }
            } catch (e:Exception){
                _error.value = e.message ?: appContext.getString(R.string.error_loading_more_recipes)
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun setRecipeType(type: String?){
        _selectedType.value = type
        resetAndLoadRecipes()
        loadRecipes() //Nueva carga inicia
    }

    private fun resetAndLoadRecipes() {
        _recipes.value = emptyList()
        currentPage = 0
        loadRecipes()
    }

    //Funciones para la Busqueda de Recetas
    fun setSearchQuery(query: String){
        _searchQuery.value = query
        if (query.length > 2){
            //Solo se ejecuta la busqueda cuando hay dos caracteres
            searchRecipes()
        } else {
            _searchResults.value = emptyList()
        }
    }

    private fun searchRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.apiService.searchRecipes(
                    query = searchQuery.value,
                    number = 10
                )
                _searchResults.value = results?.results ?: emptyList()
            } catch (e: Exception){
                _error.value = appContext.getString(R.string.error_search, e.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Funcion para limpiar Error
    fun clearError(){
        _error.value = null
    }

    //Funciones para obtener recetas aleatorias
    fun fetchRandomRecipe() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _randomRecipe.value = repository.getRandomRecipe()
            } catch (e: Exception){
                _error.value = "Error al cargar la receta aleatoria"
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Limpiar estado
    fun clearRandomRecipe() {
        _randomRecipe.value = null
    }

    /*Funciones para Manejo de Favoritos (Database)*/

    //Anade o elimina de la DB dependiendo del Icon
    suspend fun toggleFavorite(recipe: Recipe){
        if (favoriteRepository.isFavorite(recipe.id)){
            favoriteRepository.removeFavorite(recipe.id)
        } else {
            favoriteRepository.addFavorite(recipe)
        }
    }

    suspend fun toggleFavorite2(recipe: RecipeDetailResponse) {
        if (isFavorite(recipe.id)) {
            favoriteRepository.removeFavorite(recipe.id)
        } else {
            // Convierte RecipeDetailResponse a Recipe si es necesario
            val favoriteRecipe = Recipe(
                id = recipe.id,
                title = recipe.title,
                image = recipe.image
            )
            favoriteRepository.addFavorite(favoriteRecipe)
        }
    }

    //verifica si una receta especifica esta marcada como favorita
    suspend fun isFavorite(recipeId: Int): Boolean{
        return favoriteRepository.isFavorite(recipeId)
    }

    fun getFavorites(): Flow<List<Recipe>> {
        return favoriteRepository.getFavorites()
            .catch { e ->
                _error.value = e.message ?: appContext.getString(R.string.error_loading_favorites)
                emit(emptyList()) //en caso de error retornara una lista vacia
            }
    }

    fun loadRecipeDetails(id: Int){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _recipeDetail.value = repository.getRecipeDetails(id)
            } catch (e: Exception){
                _error.value = appContext.getString(R.string.error_loading_recipe_details)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
