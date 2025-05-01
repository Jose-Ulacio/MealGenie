package com.example.mealgenie.view.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealgenie.R
import com.example.mealgenie.ui.theme.quicksandFamily
import com.example.mealgenie.view.Screens.AuxiliaryComponents.EmptyState
import com.example.mealgenie.view.Screens.AuxiliaryComponents.FullScreenLoading
import com.example.mealgenie.view.Screens.AuxiliaryComponents.MainScreenStates
import com.example.mealgenie.view.Screens.AuxiliaryComponents.rememberMainScreenState
import com.example.mealgenie.viewmodel.RecipeViewModel

@Composable
fun FavoriteScreen(
    viewModel: RecipeViewModel = viewModel(),
    mainState: MainScreenStates = rememberMainScreenState(),
    onRecipeClick: (Int) -> Unit = {}

) {
    val favorites by viewModel.getFavorites().collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    var lastScrollPosition by remember { mutableIntStateOf(0) }

    //Detectar cambios en el scroll
    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress){
            val currentScrollPosition = gridState.firstVisibleItemScrollOffset
            val isScrollingDown = currentScrollPosition > lastScrollPosition

            if (isScrollingDown && mainState.isBottomBarVisible){
                mainState.updateVisibility(false)
            } else if (!isScrollingDown && !mainState.isBottomBarVisible){
                mainState.updateVisibility(true)
            }

            lastScrollPosition = currentScrollPosition
        } else {
            // Al finalizar el scroll, nos aseguramos de mostrar la barra
            if (!mainState.isBottomBarVisible){
                mainState.updateVisibility(true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding() //respetar barras de Estados
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(36.dp)
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Image(
                            modifier = Modifier
                                .align(alignment = Alignment.Center),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_heart),
                            contentDescription = "Icon Favorite",
                        )
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically),
                        text = "Favorites",
                        style = TextStyle(
                            fontFamily = quicksandFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,

                        )
                    )
                }
            }

            //Grid de Recetas Favoritas
            when {
                isLoading && favorites.isEmpty() -> FullScreenLoading()
                favorites.isEmpty() -> EmptyState(text = "No hay Favoritos")
                else -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = favorites) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { onRecipeClick(recipe.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}