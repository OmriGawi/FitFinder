package com.example.fitfinder.data.repository.profile

import com.example.fitfinder.data.model.UserProfile
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileRepository: BaseRepository() {

    private val firestore = FirebaseFirestore.getInstance()

    fun getUserProfile(userId: String): Task<DocumentSnapshot> {
        return firestore.collection("users").document(userId).get()
    }

    fun updateUserProfile(userId: String, userProfile: UserProfile): Task<Void> {
        // To update only the userProfile field, we use a map
        return firestore.collection("users").document(userId).update(mapOf("userProfile" to userProfile))
    }
}

