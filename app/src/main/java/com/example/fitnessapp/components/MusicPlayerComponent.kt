package com.example.fitnessapp.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.view_models.MusicViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicPlayerComponent(
    musicViewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val isPlaying by musicViewModel.isMusicPlaying.collectAsState()
        // .collectAsStateWithLifecycle() - не сработал, проверить!в

    var isCollapsed by rememberSaveable { mutableStateOf(false) }
    var offsetX by rememberSaveable { mutableFloatStateOf(0f) } //смещение
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }
    var widgetSize by remember { mutableStateOf(Offset.Zero) }
    var isPositionInitialized by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val containerWidthPx = constraints.maxWidth.toFloat()
        val containerHeightPx = constraints.maxHeight.toFloat()
        val edgePaddingPx = with(density) { 16.dp.toPx() }

        // границы перетаскивания
        val maxX = (containerWidthPx - widgetSize.x - edgePaddingPx).coerceAtLeast(edgePaddingPx)
        val maxY = (containerHeightPx - widgetSize.y - edgePaddingPx).coerceAtLeast(edgePaddingPx)

        LaunchedEffect(containerWidthPx, containerHeightPx, widgetSize, isPositionInitialized) {
            if (!isPositionInitialized && widgetSize.x > 0f && widgetSize.y > 0f) {
                offsetX = maxX
                offsetY = maxY
                isPositionInitialized = true
            } else {
                offsetX = offsetX.coerceIn(edgePaddingPx, maxX)
                offsetY = offsetY.coerceIn(edgePaddingPx, maxY)
            }
        }

        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .onSizeChanged { size ->
                    widgetSize = Offset(size.width.toFloat(), size.height.toFloat())
                }
                .pointerInput(maxX, maxY) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(edgePaddingPx, maxX)
                            offsetY = (offsetY + dragAmount.y).coerceIn(edgePaddingPx, maxY)
                        }
                    )
                },
            shape = if (isCollapsed) RoundedCornerShape(26.dp) else RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFB082EE).copy(alpha = 0.88f),
                                Color(0xFF195ACE).copy(alpha = 0.88f)
                            )
                        )
                    )
                    .padding(
                        horizontal = if (isCollapsed) 10.dp else 14.dp,
                        vertical = if (isCollapsed) 10.dp else 10.dp
                    )
            ) {
                if (isCollapsed) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = "Expand music player",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .combinedClickable(
                                onClick = { isCollapsed = false },
                                onLongClick = {}
                            )
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.widthIn(min = 240.dp, max = 320.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = "Collapse music player",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .combinedClickable(
                                    onClick = { isCollapsed = true },
                                    onLongClick = {}
                                )
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mount Eminest - Lofi",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isPlaying) "Now Playing" else "Paused",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(21.dp)
                                )
                                .combinedClickable(
                                    onClick = { musicViewModel.toggleMusic() },
                                    onLongClick = {}
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
