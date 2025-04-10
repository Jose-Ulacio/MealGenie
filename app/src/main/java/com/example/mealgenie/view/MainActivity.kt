package com.example.mealgenie.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mealgenie.R
import com.example.mealgenie.ui.theme.MealGenieTheme
import com.example.mealgenie.view.Screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealGenieTheme {
                MainDesign()
            }
        }
    }
}

@Composable
fun MainDesign() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(R.color.Background))
    ){
        HomeScreen()
    }
}
