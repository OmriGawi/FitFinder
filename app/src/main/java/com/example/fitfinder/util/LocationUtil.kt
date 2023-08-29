package com.example.fitfinder.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


object LocationUtil {

    interface LocationCallback {
        fun onLocationResult(location: Location?)
    }

    private fun checkLocationPermission(context: Context): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }

    fun getCurrentLocation(activity: Activity, callback: LocationCallback) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        if (checkLocationPermission(activity)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { locationResult ->
                callback.onLocationResult(locationResult)
            }.addOnFailureListener {
                callback.onLocationResult(null)
            }
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1 // Your request code
            )
        }
    }


}