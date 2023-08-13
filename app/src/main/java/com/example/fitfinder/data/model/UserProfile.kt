package com.example.fitfinder.data.model

data class UserProfile(
    val userId: String,
    val userType: UserType,
    val profilePictureUrl: String?,
    val additionalPictures: List<String>,
    val sportCategories: List<SportCategory>,
    val workoutTimes: List<WorkoutTime>,
    val description: String?
)
