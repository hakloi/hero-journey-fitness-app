package com.example.fitnessapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.view_models.CalibrationViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalibrationScreen(
    navController: NavController,
    viewModel: CalibrationViewModel
) {
    val currentAngle by viewModel.currentAngle
    val calibrationAngle by viewModel.calibrationAngle
    val isCalibrated by viewModel.isCalibrated
    val isCalibrating by viewModel.isCalibrating
    val countdown by viewModel.countdown
    val scrollState = rememberScrollState()
    val overscrollEffect = rememberOverscrollEffect()
    val menuButtonTextStyle = AppTextStyles.menuButton()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .overscroll(overscrollEffect)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Калибровка",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Текущий угол наклона:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = String.format("%.1f", currentAngle) + "°",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Инструкция:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Встаньте прямо\n" +
                        "2. Держите телефон вертикально\n" +
                        "3. Нажмите 'Калибровать'\n" +
                        "4. Замрите на 3 секунды",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isCalibrated && calibrationAngle != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Калибровка выполнена!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Запомнен угол: ${String.format("%.1f", calibrationAngle)}°",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.resetCalibration() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Сбросить",
                        style = menuButtonTextStyle
                    )
                }

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Назад",
                        style = menuButtonTextStyle
                    )
                }
            }
        } else {
            Button(
                onClick = { viewModel.startCalibration() },
                enabled = !isCalibrating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isCalibrating) "Калибровка..." else "Начать калибровку",
                    style = menuButtonTextStyle
                )
            }

            if (isCalibrating) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Замрите... $countdown",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Назад",
                    style = menuButtonTextStyle
                )
            }
        }
    }
}
