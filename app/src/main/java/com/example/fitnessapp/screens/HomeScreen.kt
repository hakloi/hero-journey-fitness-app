package com.example.fitnessapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.components.MusicPlayerComponent
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.ui.theme.SpiderSwinging
import com.example.fitnessapp.view_models.MusicViewModel

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val activity = LocalContext.current as? android.app.Activity
    val menuButtonTextStyle = AppTextStyles.menuButton()
    val scrollState = rememberScrollState()
    val overscrollEffect = rememberOverscrollEffect()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.paper_home_screen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(750.dp)
                .align(Alignment.CenterStart)
                .padding(16.dp)
        )
    }

    SpiderSwinging(
        modifier = Modifier
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .overscroll(overscrollEffect)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //        title
        Text(
            text = stringResource(R.string.gymace_home_screen),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        val buttonModifier = Modifier
            .width(250.dp)
            .height(45.dp)

        //  start patrol
        Button(onClick = { navController.navigate("patrol") },
               buttonModifier)
        {
            Text(
                stringResource(R.string.start_patrol),
                style = menuButtonTextStyle)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //  NYC map
        Button(onClick = { navController.navigate("nyc_map") },
            buttonModifier
        ) {
            Text(
                stringResource(R.string.nyc_map),
                style = menuButtonTextStyle)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //  statistics
        Button(onClick = {navController.navigate("statistics")},
            buttonModifier) {
            Text(stringResource(R.string.statistics_button),
                style = menuButtonTextStyle)
        }
        Spacer(modifier = Modifier.height(20.dp))
        // settings
        Button(onClick = {navController.navigate("settings")},
            buttonModifier) {
            Text(
                stringResource(R.string.settings_btn),
                style = menuButtonTextStyle)
        }
        Spacer(modifier = Modifier.height(20.dp))
        // quit
        Button(onClick = { activity?.finish() },
            buttonModifier) {
            Text(
                stringResource(R.string.quit_game_btn),
                style = menuButtonTextStyle)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
