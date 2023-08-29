package com.example.fitfinder.viewmodel.location

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.location.LocationRepository
import com.google.firebase.firestore.GeoPoint

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    fun updateUserLocation(userId: String, location: Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        locationRepository.updateUserLocation(userId, geoPoint)
            .addOnSuccessListener {
                // Log success
                Log.d("LocationUpdate", "Successfully updated location")
            }
            .addOnFailureListener { e ->
                // Log the error
                Log.e("LocationUpdate", "Failed to update location: ${e.message}")
            }
    }

}