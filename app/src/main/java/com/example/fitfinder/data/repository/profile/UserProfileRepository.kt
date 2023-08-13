package com.example.fitfinder.data.repository.profile

import com.example.fitfinder.data.model.UserProfile
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileRepository: BaseRepository() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userProfilesCollection = firestore.collection("userProfiles")

    fun getUserProfile(userId: String): Task<DocumentSnapshot> {
        return userProfilesCollection.document(userId).get()
    }

    fun updateUserProfile(userProfile: UserProfile): Task<Void> {
        return userProfilesCollection.document(userProfile.userId).set(userProfile)
    }

}
