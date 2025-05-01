package com.example.mealgenie.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mealgenie.view.ItemsMenu.*
import com.example.mealgenie.view.Screens.AuxiliaryComponents.MainScreenStates
import com.example.mealgenie.view.Screens.FavoriteScreen
import com.example.mealgenie.view.Screens.HomeScreen
import com.example.mealgenie.view.Screens.RecipeDetailScreen
import com.example.mealgenie.view.Screens.SearchScreen
import com.example.mealgenie.viewmodel.RecipeViewModel


@Composable
fun NavigationHost(
    navController: NavHostController,
    viewModel: RecipeViewModel,
    mainState: MainScreenStates
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route!!,
    ){
        composable(HomeScreen.route){
            LaunchedEffect(Unit) {
                mainState.updateVisibility(true)
            }
            HomeScreen(
                viewModel,
                mainState,
                onRecipeClick = {id ->
                    navController.navigate("recipe/$id")
                })
        }
        composable(FavoriteScreen.route!!){
            LaunchedEffect(Unit) {
                mainState.updateVisibility(true)
            }
            FavoriteScreen(
                viewModel,
                mainState,
                onRecipeClick = {id ->
                    navController.navigate("recipe/$id")
                })
        }
        composable(SearchScreen.route!!){
            LaunchedEffect(Unit) {
                mainState.updateVisibility(true)
            }
            SearchScreen(
                viewModel,
                mainState,
                onRecipeClick = {id ->
                    navController.navigate("recipe/$id")
                })
        }

        composable(
            route = "recipe/{recipeId}",
            arguments = listOf(navArgument("recipeId") {type = NavType.IntType})
        ){ backStackEntry ->
            LaunchedEffect(Unit) {
                mainState.updateVisibility(false)
            }

            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            RecipeDetailScreen(
                recipeId = recipeId,
                onBack = {navController.popBackStack()},
                viewModel = viewModel
            )
        }
    }
}