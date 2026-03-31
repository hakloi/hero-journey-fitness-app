package com.example.fitnessapp.ui.theme

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.R

@Composable
fun SpiderSwinging(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val movingSpider by infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = 75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = modifier.rotate(rotation),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // web
        Box (
            modifier = Modifier
                .width(2.dp)
                .height(movingSpider.dp)
                .background(Color.Black)
        )
        // spider
        Image(
            painter = painterResource(R.drawable.spider),
            contentDescription = "Spider",
            modifier = Modifier
                .size(90.dp),
            contentScale = ContentScale.Fit
        )
    }

}