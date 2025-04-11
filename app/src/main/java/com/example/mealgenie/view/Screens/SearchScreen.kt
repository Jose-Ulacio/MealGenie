package com.example.mealgenie.view.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.mealgenie.R

@Composable
fun SearchScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(R.color.Green_Primary_trans))
    ) {
        Column(modifier = Modifier.fillMaxSize()
            .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(
                text = "Esta es la Pantalla Search",
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.Black
                )
            )
        }
    }
}