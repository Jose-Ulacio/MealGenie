package com.example.mealgenie.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColors(
//    primary = Purple80,
//    secondary = PurpleGrey80
//)
//
//private val LightColorScheme = lightColors(
//    primary = GreenPrimary,
//    secondary = RedPrimary,
//    background = Background,
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onBackground = Color.Black,
//    onSurface = Color.Black
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

private val DarkColorScheme = darkColors(
    primary = Color(0xFF00A277),       // Green_Primary
    secondary = Color(0xFFA2002B),      // Red_Primary
    background = Color(0xFF121212),     // Fondo oscuro
    surface = Color(0xFF1E1E1E),       // Superficies oscuras
    onPrimary = Color.White,            // white
    onSecondary = Color.White,          // white
    onBackground = Color(0xFFEFF5F4),   // Background (claro, para contraste)
    onSurface = Color(0xFF070707),    // test_beige (para textos)
    secondaryVariant = Color(0xFF047959)
)

private val LightColorScheme = lightColors(
    primary = Color(0xFF00A277),        // Green_Primary
    secondary = Color(0xFFA2002B),      // Red_Primary
    background = Color(0xFFEFF5F4),     // Background
    surface = Color(0xFFFFFFFF),        // test_beige (para cards/superficies)
    onPrimary = Color.White,            // white
    onSecondary = Color.White,          // white
    onBackground = Color.Black,         // black (textos sobre fondo claro)
    onSurface = Color.Black,             // black (textos sobre superficies)
    secondaryVariant = Color(0xFFF5E9DC)
)

@Composable
fun MealGenieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme){
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}