package com.example.fitnessapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.R

@Composable
fun NYCMapScreen(navController: NavController) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val painter = painterResource(R.drawable.nyc_map_full)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { screenSize = it }
            .pointerInput(Unit) {
                detectDragGestures { _, drag ->
                    if (screenSize == IntSize.Zero) return@detectDragGestures
                    val intrinsic = painter.intrinsicSize
                    // масштаб: картинка по ширине экрана
                    val scale = screenSize.width / intrinsic.width
                    val renderedH = intrinsic.height * scale
                    // двигаться можно только если картинка выше экрана
                    val maxY = ((renderedH - screenSize.height) / 2f).coerceAtLeast(0f)
                    offsetX = 0f // по X картинка точно в ширину экрана — не двигаем
                    offsetY = (offsetY + drag.y).coerceIn(-maxY, maxY)
                }
            }
    ) {
        Image(
            painter = painter,
            contentDescription = "NYC Map",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(translationY = offsetY)
        )

        // сюда потом добавишь точки на карте

        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xCC000000),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("←")
        }
    }
}
