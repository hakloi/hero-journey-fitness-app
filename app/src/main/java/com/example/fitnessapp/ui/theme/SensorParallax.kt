package com.example.fitnessapp.ui.theme

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
@Composable
fun rememberParallaxOffset(
    maxOffsetX: Float = 30f,
    maxOffsetY: Float = 30f,
    sensitivity: Float = 0.5f
) : Offset {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager}
    val gravitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) }

    var targetX by remember { mutableStateOf(0f) }
    var targetY by remember { mutableStateOf(0f) }

    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val animatedY by animateFloatAsState(
        targetValue = targetY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    DisposableEffect(gravitySensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val rawX = it.values[0].coerceIn(-10f, 10f)
                    val rawY = it.values[1].coerceIn(-10f, 10f)

                    targetX = (rawX / 10f) * maxOffsetX * sensitivity
                    targetY = (rawY / 10f) * maxOffsetY * sensitivity
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return Offset(animatedX, animatedY)

}

fun Modifier.parallax(
    maxOffsetX: Float = 30f,
    maxOffsetY: Float = 30f,
    sensitivity: Float = 0.5f
): Modifier = composed {
    val offset = rememberParallaxOffset(maxOffsetX, maxOffsetY, sensitivity)

    this.then(
        Modifier.offset {
            IntOffset(
                x = offset.x.roundToInt(),
                y = offset.y.roundToInt()
            )
        }
    )
}
