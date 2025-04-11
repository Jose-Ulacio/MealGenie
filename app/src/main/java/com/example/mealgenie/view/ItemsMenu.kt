package com.example.mealgenie.view

import com.example.mealgenie.R

sealed class ItemsMenu(
    val icon: Int,
    val title: String,
    val route: String
) {
    object HomeScreen: ItemsMenu(
        icon = R.drawable.ic_home,
        title = "Home",
        route = "PantallaHome"
    )
    object FavoriteScreen: ItemsMenu(
        icon = R.drawable.ic_heart,
        title = "Favorites",
        route = "PantallaFavorites"
    )
    object SearchScreen: ItemsMenu(
        icon = R.drawable.ic_search,
        title = "Search",
        route = "PantallaSearch"
    )
}