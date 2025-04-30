package com.example.mealgenie.view.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealgenie.R
import com.example.mealgenie.data.model.Recipe
import com.example.mealgenie.view.Screens.AuxiliaryComponents.FullScreenLoading
import com.example.mealgenie.view.Screens.AuxiliaryComponents.MainScreenStates
import com.example.mealgenie.view.Screens.AuxiliaryComponents.rememberMainScreenState
import com.example.mealgenie.viewmodel.RecipeViewModel

@Composable
fun SearchScreen(
    viewModel: RecipeViewModel = viewModel(),
    mainState: MainScreenStates = rememberMainScreenState(),
    onRecipeClick: (Int) -> Unit = {}
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .height(48.dp)
            ){
                Row(modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 14.dp)
                ) {
                    Box(modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .background(
                            color = MaterialTheme.colors.surface,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(alignment = Alignment.Center),
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Icon Search",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(alignment = Alignment.CenterVertically),
                        text = "Search",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            //Barra de Busqueda
            SearchBar(
                query = searchQuery,
                onQueryChange = {viewModel.setSearchQuery(it)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            //Resultado de la Busqueda
            when {
                isLoading -> FullScreenLoading()
                searchQuery.length <= 2 -> PlaceHolderSearch()
                searchResults.isEmpty() -> EmptySearchResults()
                else -> RecipeSearchGrid(
                    recipes = searchResults,
                    viewModel = viewModel,
                    onRecipeClick = onRecipeClick
                )
            }

        }
    }
}

@Composable
fun RecipeSearchGrid(
    recipes: List<Recipe>,
    viewModel: RecipeViewModel,
    onRecipeClick: (Int) -> Unit = {}

) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
                    .clickable { onRecipeClick(recipe.id) }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(28.dp)),
        placeholder = { Text("Buscar recetas...") },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {onQueryChange("")}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Borrar",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.primary
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {  }
        )
    )
}

@Composable
fun PlaceHolderSearch() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Buscar recetas por nombre",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun EmptySearchResults() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_cookie),
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "No se encontraron recetas",
            color = MaterialTheme.colors.primary
        )
    }
}