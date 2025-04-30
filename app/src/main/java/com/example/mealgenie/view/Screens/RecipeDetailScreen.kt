package com.example.mealgenie.view.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mealgenie.R
import com.example.mealgenie.data.model.RecipeDetailResponse
import com.example.mealgenie.view.Screens.AuxiliaryComponents.ErrorMessage
import com.example.mealgenie.view.Screens.AuxiliaryComponents.FullScreenLoading
import com.example.mealgenie.viewmodel.RecipeViewModel
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
            error != null -> ErrorMessage(error!!) { viewModel.loadRecipeDetails(recipeId) }
            recipe != null -> RecipeDetailContent(
                recipe!!,
                isFavorite = isFavorite,
                onToggleFavorite = {
                    coroutineScope.launch {
                        viewModel.toggleFavorite2(recipe!!)
                        isFavorite = !isFavorite
                    }
                },
                onBack = onBack)
        }

        // Botón de retroceso flotante
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .background(
                    color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                    shape = CircleShape
                )
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colors.secondary
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
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .height(350.dp)
        ){
            AsyncImage(
                model = recipe.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
//                // Botón de retroceso
//                IconButton(
//                    onClick = onBack,
//                    modifier = Modifier
//                        .size(48.dp)
//                        .background(
//                            color = Color.White.copy(alpha = 0.9f),
//                            shape = CircleShape
//                        )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = colorResource(R.color.Green_Primary)
//                    )
//                }
                //Boton Favorito
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colors.secondary
                        else MaterialTheme.colors.secondary
                    )
                }
            }

            // Caja roja superpuesta (usando negative margin)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp) // Altura de la parte superpuesta
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colors.secondaryVariant)
            )
        }
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.secondaryVariant)
                .fillMaxWidth()
        ){
            Column {
                //Titulo
                Text(
                    text = recipe.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )

                //Informacion Basica
                RecipeMetadata(
                    time = recipe.readyInMinutes,
                    servings = recipe.servings
                )

                Column {
                    //Ingredientes
                    TextSection(
                        title = "Ingredients:",
                        items = recipe.extendedIngredients.map {
                            "${it.amount} ${it.unit} ${it.name}"
                        }
                    )
                }

                Column {
                    //Instrucciones
                    TextSection(
                        title = "Instructions:",
                        items = recipe.analyzedInstructions.flatMap { instruction ->
                            instruction.steps.map { step ->
                                "Step ${step.number}: ${step.text}"
                            }
                        }
                    )
                }
            }
        }
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
                    .clip(RoundedCornerShape(64.dp))
                    .background(MaterialTheme.colors.surface)
                    .width(64.dp)
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Column() {
                    Icon(
                        painter = painterResource(R.drawable.ic_schedule),
                        contentDescription = "time",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$time min",
                fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(64.dp))
                    .background(MaterialTheme.colors.surface)
                    .width(64.dp)
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Column() {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = "time",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$servings Servings",
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TextSection(
    title: String,
    items: List<String>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        items.forEach {item ->
            Text(
                text = "✔ $item",
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }
    }
}