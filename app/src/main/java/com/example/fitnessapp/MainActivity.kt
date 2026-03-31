package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.repository.FitnessRepository
import com.example.fitnessapp.screens.CalibrationScreen
import com.example.fitnessapp.screens.HomeScreen
import com.example.fitnessapp.screens.SquatScreen
import com.example.fitnessapp.screens.StatisticsScreen
import com.example.fitnessapp.sensors.Sensors
import com.example.fitnessapp.view_models.CalibrationViewModel
import com.example.fitnessapp.view_models.SquatCounterViewModel
import com.example.fitnessapp.view_models.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.fitnessapp.ui.theme.FitnessAppTheme

//1. учёта количества и глубины приседаний (в предположении, что телефон в руке, а рука
//поднимается во время приседа вверх);

//MVVM: view -> view model -> model
// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sensors: Sensors

    @Inject
    lateinit var repository: FitnessRepository

    private lateinit var squatViewModel: SquatCounterViewModel
    private lateinit var calibrationViewModel: CalibrationViewModel
    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sensor = sensors.getSensor(lifecycleScope)
            ?: throw RuntimeException("Gravity Sensor not found!")

        squatViewModel = SquatCounterViewModel(sensor, repository)
        calibrationViewModel = CalibrationViewModel(sensor, repository)
        statisticsViewModel = StatisticsViewModel(repository)

        setContent {
            FitnessAppTheme {
                val navController = rememberNavController()

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_miles),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f))
                    )

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") { HomeScreen(navController) }
                        composable("calibration") {
                            CalibrationScreen(navController, calibrationViewModel)
                        }
                        composable("squat") {
                            SquatScreen(navController, squatViewModel)
                        }
                        composable("squat_video") {
                            SquatScreen(navController, squatViewModel)
                        }
                        composable("statistics") {
                            StatisticsScreen(navController, statisticsViewModel)
                        }
                    }
                }
            }
        }
    }
}