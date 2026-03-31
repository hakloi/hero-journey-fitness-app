package com.example.fitnessapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.theme.MyAppFont
import com.example.fitnessapp.ui.theme.SpiderSwinging

@Composable
fun HomeScreen(navController: NavController) {


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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        title
        Text(
            text = stringResource(R.string.gymace_home_screen),
            fontFamily = MyAppFont,
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            textAlign = TextAlign.Center,

            modifier = Modifier.padding(bottom = 40.dp)
        )

        val buttonModifier = Modifier
            .width(250.dp)
            .height(45.dp)

        //        1. calibration
        Button(onClick = { navController.navigate("calibration") },
               buttonModifier)
        {
            Text(stringResource(R.string.calibration_button),
                style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //        2. squat exercise
        Button(onClick = { navController.navigate("squat") },
            buttonModifier
        ) {
            Text(stringResource(R.string.squat_button),
                style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //        3. squat with video exercise
        Button(onClick = { navController.navigate("squat_video") },
            buttonModifier) {
            Text(stringResource(R.string.squats_video_detection),
                style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))
        //      4. statistics
        Button(onClick = {navController.navigate("statistics")},
            buttonModifier) {
            Text(stringResource(R.string.statistics_button),
                style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}