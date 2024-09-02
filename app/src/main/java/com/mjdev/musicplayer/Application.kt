package com.mjdev.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    companion object {
        const val MUSIC_CHANNEL_NOTIFICATION = "music_channel_notification"
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
     private fun createNotificationChannel() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(MUSIC_CHANNEL_NOTIFICATION,"Music playback",NotificationManager.IMPORTANCE_HIGH)

        } else {
            return
        }
        notificationManager.createNotificationChannel(channel)
    }

}