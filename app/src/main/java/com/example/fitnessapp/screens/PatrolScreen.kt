package com.example.fitnessapp.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.theme.AppTextStyles

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PatrolScreen(navController: NavController) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("patrol_prefs", Context.MODE_PRIVATE)
    }
    val scrollState = rememberScrollState()
    val overscrollEffect = rememberOverscrollEffect()

    var shouldAskCalibration by rememberSaveable {
        mutableStateOf(preferences.getBoolean("ask_calibration_before_squats", true))
    }
    var showCalibrationDialog by rememberSaveable { mutableStateOf(false) }
    var dontAskAgainChecked by rememberSaveable { mutableStateOf(false) }

    val menuButtonTextStyle = AppTextStyles.menuButton()
    val regularTextStyle = AppTextStyles.regularInfo()
    val heroTitleStyle = menuButtonTextStyle.copy(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold
    )
    val cardBodyStyle = regularTextStyle.copy(
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal
    )

    fun saveAskCalibrationPreference(newValue: Boolean) {
        shouldAskCalibration = newValue
        preferences.edit().putBoolean("ask_calibration_before_squats", newValue).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .overscroll(overscrollEffect)
            .padding(horizontal = 16.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xB3161616)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.patrol_title),
                    style = heroTitleStyle,
                    color = Color(0xFFE53935),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.patrol_subtitle),
                    style = cardBodyStyle,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                MissionCard(
                    title = stringResource(R.string.mission_peters_warmup),
                    enabled = true,
                    onClick = { navController.navigate("peters_warmup") }
                )

                MissionCard(
                    title = stringResource(R.string.mission_wall_crawler),
                    enabled = false,
                    onClick = {}
                )

                MissionCard(
                    title = stringResource(R.string.mission_thunder_kick),
                    enabled = true,
                    onClick = {
                        if (shouldAskCalibration) {
                            showCalibrationDialog = true
                            dontAskAgainChecked = false
                        } else {
                            navController.navigate("squat")
                        }
                    }
                )

                MissionCard(
                    title = stringResource(R.string.mission_spider_plank),
                    enabled = false,
                    onClick = {}
                )

                MissionCard(
                    title = stringResource(R.string.mission_leap_of_faith),
                    enabled = false,
                    onClick = {}
                )

                MissionCard(
                    title = stringResource(R.string.mission_gwens_rest),
                    enabled = false,
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E1717),
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.patrol_back), style = menuButtonTextStyle)
                }
            }
        }
    }

    if (showCalibrationDialog) {
        val dialogTextStyle = cardBodyStyle.copy(fontSize = 17.sp)
        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            containerColor = Color(0xFF161616),
            title = {
                Text(
                    text = stringResource(R.string.calibration_dialog_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.calibration_dialog_text),
                        color = Color(0xFFE0E0E0),
                        style = dialogTextStyle
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Checkbox(
                            checked = dontAskAgainChecked,
                            onCheckedChange = { dontAskAgainChecked = it }
                        )
                        Text(
                            text = stringResource(R.string.calibration_dialog_dont_ask),
                            color = Color.White,
                            style = dialogTextStyle
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (dontAskAgainChecked) {
                            saveAskCalibrationPreference(false)
                        }
                        showCalibrationDialog = false
                        navController.navigate("calibration")
                    }
                ) {
                    Text(stringResource(R.string.calibration_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (dontAskAgainChecked) {
                            saveAskCalibrationPreference(false)
                        }
                        showCalibrationDialog = false
                        navController.navigate("squat")
                    }
                ) {
                    Text(stringResource(R.string.calibration_dialog_dismiss))
                }
            }
        )
    }
}

@Composable
private fun MissionCard(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFFB71C1C) else Color(0xFF3B2B2B)
        )
    ) {
        if (enabled) {
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = title,
                    style = AppTextStyles.menuButton()
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AppTextStyles.menuButton(),
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "LOCKED",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFFF8A80)
                )
            }
        }
    }
}
