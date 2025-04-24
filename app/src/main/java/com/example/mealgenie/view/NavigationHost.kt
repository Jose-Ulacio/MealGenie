package com.example.mealgenie.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mealgenie.view.ItemsMenu.*
import com.example.mealgenie.view.Screens.AuxiliaryComponents.MainScreenStates
import com.example.mealgenie.view.Screens.AuxiliaryComponents.rememberMainScreenState
import com.example.mealgenie.view.Screens.FavoriteScreen
import com.example.mealgenie.view.Screens.HomeScreen
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
        startDestination = HomeScreen.route,
    ){
        composable(HomeScreen.route){
            HomeScreen(viewModel, mainState)
        }
        composable(FavoriteScreen.route){
            FavoriteScreen(viewModel, mainState)
        }
        composable(SearchScreen.route){
            SearchScreen()
        }
    }
}