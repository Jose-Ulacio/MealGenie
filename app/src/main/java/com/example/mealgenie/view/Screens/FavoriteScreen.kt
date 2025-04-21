package com.example.mealgenie.view.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealgenie.R

@Composable
fun FavoriteScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(R.color.Green_Primary_trans))
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
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .align(alignment = Alignment.Center),
                            painter = painterResource(R.drawable.ic_heart),
                            contentDescription = "Icon Favorite",
                            tint = colorResource(R.color.Green_Primary)
                        )
                    }
                    Text(
                        modifier = Modifier.padding(start = 20.dp)
                            .align(alignment = Alignment.CenterVertically),
                        text = "Favorites",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}