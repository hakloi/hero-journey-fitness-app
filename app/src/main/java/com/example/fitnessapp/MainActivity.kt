package com.example.fitnessapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.example.fitnessapp.screens.PatrolScreen
import com.example.fitnessapp.screens.PetersWarmupScreen
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.components.MusicPlayerComponent
import com.example.fitnessapp.services.MusicService
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.view_models.MusicViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sensors: Sensors

    @Inject
    lateinit var repository: FitnessRepository

    private lateinit var squatViewModel: SquatCounterViewModel
    private lateinit var calibrationViewModel: CalibrationViewModel
    private lateinit var statisticsViewModel: StatisticsViewModel

    // music
    private var musicService : MusicService? = null
    private var isBound = false
    private lateinit var musicViewModel: MusicViewModel

    // service connection
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            musicViewModel.updateMusicState(musicService?.isPlaying() == true)

            musicViewModel.setMusicServiceCallback { shouldPlay ->
                if (shouldPlay) {
                    musicService?.playMusic()
                } else {
                    musicService?.pauseMusic()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }

    // onCreate()
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
                musicViewModel = viewModel()
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
                        composable("patrol") { PatrolScreen(navController) }
                        composable("peters_warmup") { PetersWarmupScreen(navController) }
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
                    MusicPlayerComponent(
                        musicViewModel = musicViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // жизненный цикл сервиса
    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
