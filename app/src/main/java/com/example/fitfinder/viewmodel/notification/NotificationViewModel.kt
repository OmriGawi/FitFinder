package com.example.fitfinder.viewmodel.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.notification.NotificationRepository

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    fun saveFcmToken(userId: String) {
        repository.getFcmToken().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (token != null) {
                    repository.saveFcmToken(userId, token).addOnCompleteListener { saveTask ->
                        if (saveTask.isSuccessful) {
                            Log.d("NotificationVM", "fcmToken saved successfully")
                        } else {
                            Log.e("NotificationVM", "Error saving fcmToken", saveTask.exception)
                        }
                    }
                } else {
                    Log.e("NotificationVM", "Token fetch failed")
                }
            } else {
                Log.e("NotificationVM", "Token fetch failed", task.exception)
            }
        }
    }
}
