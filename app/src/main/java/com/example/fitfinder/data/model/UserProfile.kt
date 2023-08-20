package com.example.fitfinder.data.model

data class UserProfile(
    val userId: String,
    val userType: UserType,
    val profilePictureUrl: String?,
    val additionalPictures: MutableList<String>,
    val sportCategories: MutableList<SportCategory>,
    val workoutTimes: List<WorkoutTime>,
    val description: String?
)
