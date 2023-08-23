package com.example.fitfinder.data.repository.profile

import android.net.Uri
import com.example.fitfinder.data.model.UserProfile
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.util.Constants.DEFAULT_PROFILE_PICTURE_PATH
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserProfileRepository: BaseRepository() {

    // Variables
    private val firestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    fun getUserProfile(userId: String): Task<DocumentSnapshot> {
        return firestore.collection("users").document(userId).get()
    }

    fun getDefaultProfilePictureUrl(): Task<Uri> {
        val defaultImageRef = storageRef.child(DEFAULT_PROFILE_PICTURE_PATH)
        return defaultImageRef.downloadUrl
    }

    fun uploadProfilePicture(userId: String, uri: Uri): Task<Uri> {
        val profilePicRef = storageRef.child("users/$userId/profilePictureUrl/${uri.lastPathSegment}")
        return profilePicRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@continueWithTask profilePicRef.downloadUrl
        }
    }

    fun uploadAdditionalPicture(userId: String, uri: Uri): Task<Uri> {
        val additionalPicRef = storageRef.child("users/$userId/additionalPictures/${uri.lastPathSegment}")
        return additionalPicRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@continueWithTask additionalPicRef.downloadUrl
        }
    }

    fun updateUserProfile(userId: String, userProfile: UserProfile): Task<Void> {
        // To update only the userProfile field, we use a map
        return firestore.collection("users").document(userId).update(mapOf("userProfile" to userProfile))
    }
}

