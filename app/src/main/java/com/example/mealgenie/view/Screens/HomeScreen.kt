package com.example.mealgenie.view.Screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.example.mealgenie.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun HomeScreen(viewModel: RecipeViewModel = viewModel()) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    val gridState = rememberLazyStaggeredGridState()

    //Detectar cuando llegue al final de la Pagina
    val isAtBottom = remember {
        derivedStateOf {
        gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == gridState.layoutInfo.totalItemsCount -1
        }
    }

    //Empieza a cargar mas recetas cuando se cumplen las condiciones
    LaunchedEffect(isAtBottom) {
        if (isAtBottom.value && !isLoadingMore && !isLoading){
            viewModel.loadMoreRecipes()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(R.color.Background))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding() //respetar barras de Estados
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
                            color = Color.White,
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
                    viewModel.setRecipeType(if (type == "All") null else type)}
            )

            //Grid de Recetas
            when{
                isLoading && recipes.isEmpty() -> { FullScreenLoading() }
                recipes.isEmpty() -> { EmptyState() }
                else -> {
                    RecipeGrid(
                        recipe = recipes,
                        gridState = gridState,
                        isLoadingMore = isLoadingMore,
                        viewModel = viewModel
                    )
                }
            }
        }

        //Manejo de Errores
        error?.let {errorMessage ->
            ErrorMessage(errorMessage) {viewModel.clearError() }
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
                        if (isSelected) colorResource(R.color.Red_Primary)
                        else Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.Red_Primary),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTypeSelected(item) }
                    .padding(horizontal = 12.dp)
            ){
                Text(
                    text = item,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else colorResource(R.color.Red_Primary)
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier) {

    var isChecked by remember { mutableStateOf(false) }

    //Comprobar si es favorito al iniciar
    LaunchedEffect(recipe.id) {
        isChecked = viewModel.isFavorite(recipe.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Random.nextInt(100,300).dp)

            ){
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
                    modifier = modifier.fillMaxHeight(0.5f)
                        .align(Alignment.BottomEnd)
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 1f)
                                    ),
                                    startY = 0.5f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                }

                Text(
                    text = recipe.title,
                    color = Color.Black,
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
                     modifier = Modifier.size(48.dp)
                         .align(Alignment.TopEnd),
                ) {
                     Icon(
                         painter = painterResource(
                             id = if(isChecked){
                                 R.drawable.favorite_24dp_fill
                             } else {
                                 R.drawable.favorite_24dp_outline
                             }
                         ),
                         contentDescription = null,
                         tint = colorResource(R.color.Red_Primary),
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
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()){
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = recipe
            ){recipe ->
                RecipeCard(
                    recipe = recipe,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //Footer de Carga
            if (isLoadingMore){
                item{
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando mas Recetas...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

    }
}

//Componentes Auxiliares
