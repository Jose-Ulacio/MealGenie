package com.example.mealgenie.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealgenie.R
import com.example.mealgenie.ui.theme.Background
import com.example.mealgenie.ui.theme.MealGenieTheme
import com.example.mealgenie.view.ItemsMenu.*
import com.example.mealgenie.view.Screens.AuxiliaryComponents.rememberMainScreenState
import com.example.mealgenie.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(false) }
            val viewModel: RecipeViewModel = viewModel(factory = object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipeViewModel(applicationContext) as T
                }
            })

            MealGenieTheme(darkTheme = isDarkMode.value ) {
                MainDesign(
                    onDarkModeToggle = {isDarkMode.value = !isDarkMode.value},
                    isDarkMode = isDarkMode.value,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun MainDesign(
    onDarkModeToggle: () -> Unit,
    isDarkMode: Boolean,
    viewModel: RecipeViewModel
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val mainState = rememberMainScreenState()
    val randomRecipe by viewModel.randomRecipe.collectAsState()

    //Estado para controlar la visibilidad del FAB
    val showFab = remember { mutableStateOf(false) }

    //Observar cambios en la ruta Actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //Actualizar showFab basado en la ruta actual
    LaunchedEffect(currentRoute) {
        showFab.value = currentRoute == HomeScreen.route
    }

    LaunchedEffect(randomRecipe) {
        randomRecipe?.let { recipe ->
            navController.navigate("recipe/${recipe.id}")
            viewModel.clearRandomRecipe() // Resetear después de navegar
        }
    }

    // Función del FAB
    fun navigateToRandomRecipe() {
        viewModel.fetchRandomRecipe()
    }

    val navigationItem = listOf(
        HomeScreen,
        FavoriteScreen,
        SearchScreen,
        DarkModeButtom
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Background)
    ){
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                AnimatedVisibility(
                    visible = mainState.isBottomBarVisible,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        NavegacionInferior(
                            navController = navController,
                            navigationItem = navigationItem,
                            onDarkModeToggle = onDarkModeToggle
                        )
                    }
                }
            },
            modifier = Modifier.background(Color.Transparent),
            content = {padding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ){
                    NavigationHost(navController, viewModel = viewModel, mainState)
                }
            }
        )

        //FAB solo visible en la HomeScreen
        if (showFab.value && mainState.isBottomBarVisible) {
            Box(
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primary)
                        .border(
                            width = 6.dp,
                            color = MaterialTheme.colors.background,
                            shape = CircleShape
                        )
                        .clickable { navigateToRandomRecipe() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dice),
                        contentDescription = "Random Recipe",
                        tint = MaterialTheme.colors.surface,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val currentRoute by navController.currentBackStackEntryAsState()
    return currentRoute?.destination?.route
}

@Composable
fun NavegacionInferior(
    navController: NavHostController,
    navigationItem: List<ItemsMenu>,
    onDarkModeToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.surface
        ) {
            BottomNavigation(
                modifier = Modifier
                    .height(56.dp) // Altura estándar para BottomNavigation
                    .background(Color.Transparent),
                elevation = 0.dp, // Eliminamos la elevación interna
                backgroundColor = Color.Transparent // Fondo transparente
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavigation(modifier = Modifier.weight(1f),
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp)
                    {
                        val currentRoute = currentRoute(navController = navController)

                        navigationItem.forEach { item ->
                            BottomNavigationItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    if (item is DarkModeButtom){
                                        onDarkModeToggle()
                                    } else {
                                        item.route?.let { navController.navigate(it) }
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = item.icon),
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                alwaysShowLabel = false,
                                selectedContentColor = MaterialTheme.colors.primary,
                                unselectedContentColor = MaterialTheme.colors.primary.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}


