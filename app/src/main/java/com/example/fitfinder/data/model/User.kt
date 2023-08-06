package com.example.fitfinder.data.model

import com.google.firebase.Timestamp

data class User(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val birthDate: Timestamp,
    val email: String,
)
