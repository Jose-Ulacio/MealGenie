package com.example.mealgenie.view.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mealgenie.R
import com.example.mealgenie.data.model.Recipe
import com.example.mealgenie.view.Screens.AuxiliaryComponents.EmptyState
import com.example.mealgenie.view.Screens.AuxiliaryComponents.ErrorMessage
import com.example.mealgenie.view.Screens.AuxiliaryComponents.FullScreenLoading
import com.example.mealgenie.view.Screens.AuxiliaryComponents.MainScreenStates
import com.example.mealgenie.view.Screens.AuxiliaryComponents.rememberMainScreenState
import com.example.mealgenie.viewmodel.RecipeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: RecipeViewModel = viewModel(),
    mainState: MainScreenStates = rememberMainScreenState(),
    onRecipeClick: (Int) -> Unit = {}
) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val gridState = rememberLazyStaggeredGridState()
    var lastScrollPosition by remember { mutableIntStateOf(0) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    //Detectar cambios en el scroll
    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress) {
            val currentScrollPosition = gridState.firstVisibleItemScrollOffset
            val isScrollingDown = currentScrollPosition > lastScrollPosition

            if (isScrollingDown && mainState.isBottomBarVisible) {
                mainState.updateVisibility(false)
            } else if (!isScrollingDown && !mainState.isBottomBarVisible) {
                mainState.updateVisibility(true)
            }

            lastScrollPosition = currentScrollPosition
        } else {
            // Al finalizar el scroll, nos aseguramos de mostrar la barra
            if (!mainState.isBottomBarVisible) {
                mainState.updateVisibility(true)
            }
        }
    }

    //Detectar Scroll para la Paginacion
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val lastItemVisible = layoutInfo.visibleItemsInfo.lastOrNull()

            lastItemVisible?.index != null &&
                    lastItemVisible.index >= layoutInfo.totalItemsCount - 2 &&
                    !viewModel.isLoadingMore.value
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadMoreRecipes()
        }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.refreshRecipe() },
        modifier = Modifier
            .background(MaterialTheme.colors.background)
    ) {
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
                                imageVector = ImageVector.vectorResource(R.drawable.ic_home),
                                contentDescription = "Icon Home",
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        Text(
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically),
                            text = "Home",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                //Lista Dinamica de Filtros
                RecipeTypeChips(
                    selectedType = selectedType,
                    onTypeSelected = { type ->
                        viewModel.setRecipeType(if (type == "All") null else type)
                    }
                )

                //Grid de Recetas
                when {
                    isLoading && recipes.isEmpty() -> {
                        FullScreenLoading()
                    }

                    recipes.isEmpty() -> {
                        EmptyState()
                    }

                    else -> {
                        RecipeGrid(
                            recipe = recipes,
                            gridState = gridState,
                            isLoadingMore = isLoadingMore,
                            viewModel = viewModel,
                            onRecipeClick = onRecipeClick
                        )
                    }
                }
            }

            //Manejo de Errores
            error?.let { errorMessage ->
                ErrorMessage(errorMessage) { viewModel.clearError() }
            }
        }
    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeTypeChips(
    selectedType: String?,
    onTypeSelected: (String) -> Unit
) {
    val chipItems = remember {
        listOf(
            "All",
            "Main Course",
            "Side Dish",
            "Dessert",
            "Appetizer",
            "Salad",
            "Bread",
            "Breakfast",
            "Soup",
            "Beverage",
            "Sauce",
            "Marinade",
            "Fingerfood",
            "Snacks",
            "Drink"
        )
    }

    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        chipItems.forEach { item ->
            val isSelected = selectedType == item || (selectedType == null && item == "All")

            //Personalizacion del Chip (Hecho con una Box)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) MaterialTheme.colors.secondary
                        else Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTypeSelected(item) }
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item,
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colors.onPrimary
                    else MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
        .fillMaxWidth()
) {

    var isChecked by remember { mutableStateOf(false) }

    val cardHeight = remember(recipe.id) { (100..300).random().dp }

    //Comprobar si es favorito al iniciar
    LaunchedEffect(recipe.id) {
        isChecked = viewModel.isFavorite(recipe.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)

            ) {
                //Imagen de la receta
                AsyncImage(
                    model = recipe.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    error = painterResource(R.drawable.baseline_broken_image_24)
                )

                Box(
                    modifier = modifier
                        .fillMaxHeight(0.5f)
                        .align(Alignment.BottomEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colors.onPrimary.copy(alpha = 1f)
                                    ),
                                    startY = 0.5f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                }

                Text(
                    text = recipe.title,
                    color = MaterialTheme.colors.onSurface,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconToggleButton(
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        isChecked = checked
                        viewModel.viewModelScope.launch {
                            viewModel.toggleFavorite(recipe)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopEnd),
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isChecked) {
                                R.drawable.favorite_24dp_fill
                            } else {
                                R.drawable.favorite_24dp_outline
                            }
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeGrid(
    recipe: List<Recipe>,
    gridState: LazyStaggeredGridState,
    isLoadingMore: Boolean,
    viewModel: RecipeViewModel,
    onRecipeClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = recipe
            ) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onRecipeClick(recipe.id) }
                )
            }
            item() {
                if (isLoadingMore) {
                    LoadingMoreItem()
                }
            }
        }
    }

    //Footer de Carga
    if (isLoadingMore) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cargando más recetas...",
                style = TextStyle(
                    color = MaterialTheme.colors.primary,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun LoadingMoreItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colors.primary,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Loading More Recipes...",
            color = MaterialTheme.colors.primary
        )
    }
}

