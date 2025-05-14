package com.example.mealgenie.view.Screens.AuxiliaryComponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealgenie.R
import com.example.mealgenie.ui.theme.Background
import com.example.mealgenie.ui.theme.quicksandFamily

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(text: String = "No Recipes Found") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = quicksandFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
fun ErrorMessage(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Error",
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        },
        text = {
            Text(
                text = error,
                style = TextStyle(
                    fontFamily = quicksandFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = "OK",
                    style = TextStyle(
                        fontFamily = quicksandFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    )
}

@Composable
fun DashLine(
    color: Color = MaterialTheme.colors.primary,
    step: Dp = 10.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(step.toPx(), step.toPx()),
                    phase = 0f
                )
            )
        }
    }
}

@Composable
fun SemiCircle() {
    val backgroundColor = MaterialTheme.colors.background

    Canvas(
        modifier = Modifier.size(30.dp)
    ) {
        val reducedHeight = size.height * 0.8f
        drawArc(
            color = backgroundColor,
            startAngle = 0f,  // Ángulo inicial (parte superior izquierda)
            sweepAngle = 180f,  // Barre 180 grados (media circunferencia)
            useCenter = true,   // Conecta al centro para formar semicírculo
            size = Size(size.width, reducedHeight),
            // Ajustamos la posición vertical para que el "centro" quede más abajo
            topLeft = Offset(0f, size.height - reducedHeight),
            style = Fill   // Relleno sólido
        )
    }
}

@Composable
fun CircleWithArrows() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_down_double_fill),
            contentDescription = "Next Step",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}