package com.example.fitfinder.data.repository.notification

import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class NotificationRepository: BaseRepository() {

    // Variables
    private val firestore = FirebaseFirestore.getInstance()

    // Get the current FCM token
    fun getFcmToken(): Task<String> {
        return FirebaseMessaging.getInstance().token
    }

    // Save the FCM token to Firestore under the user's document
    fun saveFcmToken(userId: String, token: String): Task<Void> {
        val documentReference = firestore.collection("users").document(userId)
        return documentReference.update("fcmToken", token)
    }
}
