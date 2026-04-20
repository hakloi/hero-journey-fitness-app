package com.example.fitnessapp.screens

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.services.ReminderReceiver
import com.example.fitnessapp.ui.theme.AppTextStyles
import java.util.*

val BG_DRAWABLES = listOf(
    R.drawable.bg_miles,
    R.drawable.bg_miguel,
    R.drawable.bg_miles_morales,
    R.drawable.bg_gwen_miles,
    R.drawable.bg_gwen,
    R.drawable.bg_gwen_2,
    R.drawable.bg_spiderman,
    R.drawable.bg_liberty_statue
)

val SoftCyan = Color(0xFF4DD0E1).copy(alpha = 0.75f)

@Composable
fun SettingsScreen(navController: NavController, onBgChange: (Int) -> Unit = {}) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE) }

    var reminderEnabled by remember { mutableStateOf(prefs.getBoolean("reminder_enabled", false)) }
    var reminderHour by remember { mutableIntStateOf(prefs.getInt("reminder_hour", 9)) }
    var reminderMinute by remember { mutableIntStateOf(prefs.getInt("reminder_minute", 0)) }
    var bgIndex by remember { mutableIntStateOf(prefs.getInt("bg_index", 0)) }

    val menuButtonTextStyle = AppTextStyles.menuButton()
    val regularInfoTextStyle = AppTextStyles.regularInfo()

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            reminderEnabled = true
            prefs.edit { putBoolean("reminder_enabled", true) }
            scheduleReminder(context, reminderHour, reminderMinute)
        }
    }

    fun tryEnableReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        reminderEnabled = true
        prefs.edit { putBoolean("reminder_enabled", true) }
        scheduleReminder(context, reminderHour, reminderMinute)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = { navController.navigateUp() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.return_button), style = menuButtonTextStyle)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Фон ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xB3161616))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.settings_bg_title),
                    color = Color.White,
                    style = regularInfoTextStyle,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    bgIndex = (bgIndex - 1 + BG_DRAWABLES.size) % BG_DRAWABLES.size
                    prefs.edit { putInt("bg_index", bgIndex) }
                    onBgChange(bgIndex)
                }) {
                    Text("‹", style = menuButtonTextStyle, color = Color.White)
                }
                Text(
                    "${bgIndex + 1}",
                    style = regularInfoTextStyle,
                    color = SoftCyan,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = {
                    bgIndex = (bgIndex + 1) % BG_DRAWABLES.size
                    prefs.edit { putInt("bg_index", bgIndex) }
                    onBgChange(bgIndex)
                }) {
                    Text("›", style = menuButtonTextStyle, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Напоминание ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xB3161616))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_reminder_enable), color = Color(0xFFE0E0E0), style = regularInfoTextStyle)
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) tryEnableReminder()
                            else {
                                reminderEnabled = false
                                prefs.edit { putBoolean("reminder_enabled", false) }
                                cancelReminder(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFE53935),
                            checkedTrackColor = Color(0xFF8E1717)
                        )
                    )
                }

                if (reminderEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.settings_reminder_time, reminderHour, reminderMinute),
                            color = SoftCyan,
                            style = regularInfoTextStyle
                        )
                        Button(
                            onClick = {
                                TimePickerDialog(context, { _, h, m ->
                                    reminderHour = h
                                    reminderMinute = m
                                    prefs.edit { putInt("reminder_hour", h); putInt("reminder_minute", m) }
                                    scheduleReminder(context, h, m)
                                }, reminderHour, reminderMinute, true).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftCyan)
                        ) {
                            Text(
                                stringResource(R.string.settings_reminder_change_time),
                                style = regularInfoTextStyle,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

fun scheduleReminder(context: Context, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0,
        Intent(context, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}

fun cancelReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0,
        Intent(context, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}
