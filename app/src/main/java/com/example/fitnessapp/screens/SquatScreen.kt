package com.example.fitnessapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.view_models.SquatCounterViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SquatScreen(
    navController: NavController,
    squatViewModel: SquatCounterViewModel,
    modifier: Modifier = Modifier
) {
    val totalCount by squatViewModel.totalCount
    val currentSessionCount by squatViewModel.currentSessionCount
    val currentAngle by squatViewModel.currentAngle
    val scrollState = rememberScrollState()
    val overscrollEffect = rememberOverscrollEffect()
    val menuButtonTextStyle = AppTextStyles.menuButton()
    val hasCalibration = squatViewModel.hasCalibration()
    val calibrationAngle = squatViewModel.getCalibrationAngle()

    // difference
    val deltaAngle = currentAngle - calibrationAngle

    DisposableEffect(Unit) {
        onDispose {
            squatViewModel.resetCount()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .overscroll(overscrollEffect)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.squat_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Предупреждение, если нет калибровки
        if (!hasCalibration) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            )
            {
                Text(
                    text = stringResource(R.string.no_calibration),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Button(
                onClick = { navController.navigate("calibration") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.to_calibration_button),
                    style = menuButtonTextStyle
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.return_button),
                    style = menuButtonTextStyle
                )
            }

            return@Column
        }

        // Счетчик приседаний
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),  // фиксированная высота
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.local_total_squats_txt),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "$totalCount",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = stringResource(R.string.local_workout, currentSessionCount),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Данные об угле
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),  // фиксированная высота
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.angle_phone),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = String.format("%.1f", currentAngle) + "°",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = stringResource(
                            R.string.calibration_txt,
                            String.format("%.1f", calibrationAngle)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Индикатор изменения угла
                    val deltaColor = when {
                        deltaAngle > 30 -> MaterialTheme.colorScheme.error
                        deltaAngle > 15 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Text(
                        text = stringResource(R.string.angle, String.format("%.1f", deltaAngle)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = deltaColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when {
                        deltaAngle > 30 -> {
                            Text(
                                text = stringResource(R.string.squat_txt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        deltaAngle > 15 -> {
                            Text(
                                text = stringResource(R.string.in_progress),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        else -> {
                            Text(
                                text = stringResource(R.string.stand),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { squatViewModel.saveCurrentSession() },
                modifier = Modifier.weight(1f),
                enabled = currentSessionCount > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.save_button),
                    style = menuButtonTextStyle
                )
            }

            Button(
                onClick = { squatViewModel.resetCount() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(R.string.reset_btn),
                    style = menuButtonTextStyle
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.return_button),
                style = menuButtonTextStyle
            )
        }

        // Кнопка - Калибровка (возможность откалиброваться, даже если уже были внесены данные)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("calibration") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = stringResource(R.string.calibration_button),
                style = menuButtonTextStyle
            )
        }

        // Подсказка
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.advice) +
                        stringResource(R.string.advice_angle),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
