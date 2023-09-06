package com.example.fitfinder.data.model

data class PotentialUser(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val age: Int,  // Calculated from birthDate
    val userType: UserType?,
    val profilePictureUrl: String?,
    val additionalPictures: List<String> = emptyList(),
    val sportCategories: List<SportCategory> = emptyList(),
    val workoutTimes: List<WorkoutTime> = emptyList(),
    val description: String? = null,
    val distance: Float? = null  // Calculated based on GeoPoint
)
