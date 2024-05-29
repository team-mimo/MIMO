package com.mimo.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.mimo.android.services.health.HealthConnectManager

class BaseApplication: Application(){
    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "running_channel",
            "Running Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}