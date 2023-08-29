package com.example.fitfinder.data.repository.location

import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class LocationRepository : BaseRepository(){
    private val firestore = FirebaseFirestore.getInstance()

    fun updateUserLocation(userId: String, location: GeoPoint): Task<Void> {
        return firestore.collection("users").document(userId)
            .update("location", location)
    }

}