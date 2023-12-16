package com.example.fitfinder.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fitfinder.R
import com.example.fitfinder.ui.auth.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
//TODO: override onNewToken instead of SuppressLint (Only If Needed)
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val MATCH_CHANNEL_ID = "match_notifications_channel"
        private const val INVITE_CHANNEL_ID = "invite_notifications_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received: ${remoteMessage.data}")

        when (remoteMessage.data["type"]) {
            "match" -> handleMatchNotification(remoteMessage)
            "invite" -> handleInviteNotification(remoteMessage)
            // Add more handlers for other types as necessary...
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val matchChannel = NotificationChannel(MATCH_CHANNEL_ID, "Match Notifications", NotificationManager.IMPORTANCE_HIGH ).apply {
                description = "Notifications regarding user matches."
            }

            val inviteChannel = NotificationChannel(INVITE_CHANNEL_ID, "Invite Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for training session invites."
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(matchChannel)
            notificationManager.createNotificationChannel(inviteChannel)
            // Create more channels as necessary...
        }
    }


    private fun handleMatchNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "New Match!"
        val messageBody = remoteMessage.notification?.body ?: "You have a new match. Check it out!"

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, MATCH_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun handleInviteNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: "Training Invite"
        val messageBody = remoteMessage.data["body"] ?: "You've received a new training invite!"

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, INVITE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build())
    }


}
