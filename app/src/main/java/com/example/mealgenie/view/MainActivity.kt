package com.example.mealgenie.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealgenie.R
import com.example.mealgenie.ui.theme.MealGenieTheme
import com.example.mealgenie.view.Screens.HomeScreen
import com.example.mealgenie.view.ItemsMenu.*
import com.example.mealgenie.view.Screens.FavoriteScreen
import com.example.mealgenie.view.Screens.HomeScreen
import com.example.mealgenie.view.Screens.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(false) }

            MealGenieTheme(darkTheme = isDarkMode.value ) {
                MainDesign(
                    onDarkModeToggle = {isDarkMode.value = !isDarkMode.value},
                    isDarkMode = isDarkMode.value
                )
            }
        }
    }
}

@Composable
fun MainDesign(
    onDarkModeToggle: () -> Unit,
    isDarkMode: Boolean
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val navigationItem = listOf(
        HomeScreen,
        FavoriteScreen,
        SearchScreen
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(R.color.Green_Primary_trans))
    ){
        Scaffold(
            scaffoldState = scaffoldState,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(R.color.Green_Primary_trans))
                ) {
                    NavegacionInferior(
                        navController = navController,
                        navigationItem = navigationItem,
                        onDarkModeToggle = onDarkModeToggle,
                        isDarkMode = isDarkMode)
                }
            },
            modifier = Modifier.background(Color.Transparent),
            content = {padding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ){
                    NavigationHost(navController)
                }
            }
        )
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
    onDarkModeToggle: () -> Unit,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // Añadimos una sombra más pronunciada para el efecto de elevación
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true
                ),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
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
                                onClick = { navController.navigate(item.route) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = item.icon),
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                alwaysShowLabel = false,
                                selectedContentColor = colorResource(R.color.Green_Primary),
                                unselectedContentColor = colorResource(R.color.Green_Primary_trans)
                            )
                        }
                    }
                    //Boton Modo Oscuro
                    IconButton(
                        onClick = onDarkModeToggle,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.moon_fill),
                            contentDescription = "Modo Oscuro",
                            tint = colorResource(R.color.Green_Primary_trans)
                        )
                    }
                }
            }
        }
    }
}



