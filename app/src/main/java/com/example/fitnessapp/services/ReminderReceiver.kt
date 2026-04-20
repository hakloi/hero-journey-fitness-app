package com.example.fitnessapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.R
import com.example.fitnessapp.screens.scheduleReminder

class ReminderReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "workout_reminder"
        manager.createNotificationChannel(
            NotificationChannel(channelId, "Workout Reminder", NotificationManager.IMPORTANCE_HIGH)
        )
        manager.notify(1,
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.spider)
                .setContentTitle("Hero Training Journey")
                .setContentText("Time to train, hero! 🕷️")
                .setAutoCancel(true)
                .build()
        )

        val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("reminder_enabled", false)) return
        val hour = prefs.getInt("reminder_hour", 9)
        val minute = prefs.getInt("reminder_minute", 0)
        scheduleReminder(context, hour, minute)
    }
}
