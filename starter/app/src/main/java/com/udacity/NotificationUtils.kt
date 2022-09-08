package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(data: NotificationData,  context: Context){

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtra("repositoryName", data.repositoryName)
    contentIntent.putExtra("isSuccess", data.isSuccess)

    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    val builder = NotificationCompat.Builder(context, context.getString(R.string.download_notification_channel_ID))
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            pendingIntent
        )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(data.title)
        .setContentText(data.message)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.description = channelName
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }
}