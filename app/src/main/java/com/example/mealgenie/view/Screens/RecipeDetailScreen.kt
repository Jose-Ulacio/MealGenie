package com.example.mealgenie.view.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.example.mealgenie.R
import com.example.mealgenie.data.model.RecipeDetailResponse
import com.example.mealgenie.ui.theme.BlankAlpha10
import com.example.mealgenie.ui.theme.quicksandFamily
import com.example.mealgenie.view.Screens.AuxiliaryComponents.CircleWithArrows
import com.example.mealgenie.view.Screens.AuxiliaryComponents.ErrorMessage
import com.example.mealgenie.view.Screens.AuxiliaryComponents.FullScreenLoading
import com.example.mealgenie.view.Screens.AuxiliaryComponents.SemiCircle
import com.example.mealgenie.viewmodel.RecipeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    viewModel: RecipeViewModel,
    onBack: () -> Unit
) {
    val recipe by viewModel.recipeDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var isFavorite by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val colorGreenPrimary = MaterialTheme.colors.primary

    DisposableEffect(systemUiController) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = colorGreenPrimary
        )

        // setStatusBarColor() and setNavigationBarColor() also exist

        onDispose {}
    }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetails(recipeId)

        //verifica el estado de Favorito
        isFavorite = viewModel.isFavorite(recipeId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        // Contenido principal
        when {
            isLoading -> FullScreenLoading()
            error != null -> ErrorMessage(
                error = error!!,
                onDismiss = {
                    viewModel.clearError()
                    onBack()
                }
            )
            recipe != null -> RecipeDetailContent(
                recipe!!,
                isFavorite = isFavorite,
                onToggleFavorite = {
                    coroutineScope.launch {
                        viewModel.toggleFavorite2(recipe!!)
                        isFavorite = !isFavorite
                    }
                },
                onBack = onBack
            )
        }
    }
}

@Composable
fun RecipeDetailContent(
    recipe: RecipeDetailResponse,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean

) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        //Caja donde estara la Imagen y la caja con Metadata
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(154.dp)
            ) {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón de retroceso
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onBack() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_left_fill),
                            contentDescription = "Back",
                            tint = colorResource(R.color.Green_Primary),
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    // Botón de Favorito
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onToggleFavorite() }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colors.secondary
                            else MaterialTheme.colors.secondary,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            //Box para los MetaData
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(74.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Titulo
                    Text(
                        text = recipe.title,
                        style = TextStyle(
                            fontFamily = quicksandFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colors.primaryVariant,
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    //Informacion Basica
                    RecipeMetadata(
                        time = recipe.readyInMinutes,
                        servings = recipe.servings
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 10.dp)
                .background(Color.Transparent)
                .fillMaxWidth()
        ) {
            Text(
                text = "Ingredients",
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colors.onSurface
                )
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .background(
                    MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)

        ) {
            IngredientSection(
                items = recipe.extendedIngredients.map {
                    "${it.amount} ${it.unit} ${it.name}"
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 10.dp)
                .background(Color.Transparent)
                .fillMaxWidth()
        ) {
            Text(
                text = "Instructions",
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = MaterialTheme.colors.onSurface
                )
            )
        }

        //seccion de Instruccion
        InstructionSection(
            items = recipe.analyzedInstructions.flatMap { instruction ->
                instruction.steps.map { step ->
                    step.text
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RecipeMetadata(
    time: Int,
    servings: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primaryVariant)
                    .width(24.dp)
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Icon(
                        painter = painterResource(R.drawable.ic_schedule),
                        contentDescription = "time",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$time min",
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 6.sp,
                    color = MaterialTheme.colors.primaryVariant
                )
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primaryVariant)
                    .width(24.dp)
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = "time",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$servings Servings",
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 6.sp,
                    color = MaterialTheme.colors.primaryVariant
                )
            )
        }
    }
}

@Composable
fun IngredientSection(
    items: List<String>,
    column: Int = 2
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        val middleIndex = (items.size + 1) / column
        val firstColumnItems = items.take(middleIndex)
        val secondColumnItems = items.drop(middleIndex)

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            //Ref de los Elementos
            val (col1, divider, col2) = createRefs()



            //Primera Columna
            Column(modifier = Modifier
                .constrainAs(col1) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.percent(0.45f)
                }
            ) {
                firstColumnItems.forEach { item ->
                    Text(
                        text = "• $item",
                        style = TextStyle(
                            fontFamily = quicksandFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 8.sp,
                            color = MaterialTheme.colors.onSecondary
                        )
                    )
                }
            }

            //Divider
            Box(
                modifier = Modifier
                    .background(BlankAlpha10)
                    .constrainAs(divider){
                        start.linkTo(col1.end)
                        end.linkTo(col2.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
                    .width(1.dp),
            ) {  }

            // Segunda columna
            Column(
                modifier = Modifier
                    .constrainAs(col2) {
                        start.linkTo(divider.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.percent(0.45f)
                    }
            ) {
                secondColumnItems.forEach { item ->
                    Text(
                        text = "• $item",
                        style = TextStyle(
                            fontFamily = quicksandFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 8.sp,
                            color = MaterialTheme.colors.onSecondary
                        )
                    )
                }
            }
        }
    }


//    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
//        val chunkedItems = items.chunked((items.size + column - 1) / column)
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            chunkedItems.forEach { columnItems ->
//                Column(modifier = Modifier.weight(1f)) {
//                    columnItems.forEach { item ->
//                        Text(
//                            text = "• $item",
//                            style = TextStyle(
//                                fontFamily = quicksandFamily,
//                                fontWeight = FontWeight.Normal,
//                                fontSize = 8.sp,
//                                color = MaterialTheme.colors.onSecondary
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
}

@Composable
fun InstructionSection(
    items: List<String>
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items.forEachIndexed { index, instruction ->
                // Determinar si mostrar semicírculo o círculo con flecha
                val showTopSemiCircle = index > 0
                val showBottomCircle = index < items.size - 1

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.surface,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colors.primary,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = TextStyle(
                                        fontFamily = quicksandFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 6.sp,
                                        color = MaterialTheme.colors.primary
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Text(
                                text = instruction,
                                style = TextStyle(
                                    fontFamily = quicksandFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colors.onSecondary
                                )
                            )
                        }
                    }
                    // Semicírculo superior (excepto primera caja)
                    if (showTopSemiCircle) {
                        Box(
                            modifier = Modifier
                                .align(alignment = Alignment.TopCenter)
                                .offset(y = (-18).dp) // Mitad fuera de la caja
                        ) {
                            SemiCircle()
                        }
                    }

                    // Círculo con flecha inferior (excepto última caja)
                    if (showBottomCircle) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp) // Mitad dentro de la caja
                        ) {
                            CircleWithArrows()
                        }
                    }
                }
            }
        }
    }
}

