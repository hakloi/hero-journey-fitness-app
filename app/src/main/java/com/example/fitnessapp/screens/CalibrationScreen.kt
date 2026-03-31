package com.example.fitnessapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.view_models.CalibrationViewModel

@SuppressLint("DefaultLocale")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "Калибровка",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Текущий угол
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

        // Инструкция
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
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

        // Отображение калибровки
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
                        text = "✓ Калибровка выполнена!",
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
                    Text("Сбросить")
                }

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Назад")
                }
            }
        } else {
            // Кнопка калибровки
            Button(
                onClick = { viewModel.startCalibration() },
                enabled = !isCalibrating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isCalibrating) "Калибровка..." else "Начать калибровку")
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
                Text("Назад")
            }
        }
    }
}