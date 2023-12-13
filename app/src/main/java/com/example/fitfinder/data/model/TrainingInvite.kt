package com.example.fitfinder.data.model
import com.google.firebase.Timestamp

data class TrainingInvite(
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val sportCategory: String,
    val exercises: List<String>,
    val dateTime: Timestamp,
    val location: String,
    val additionalEquipment: String,
    var status: String = "sent"
)

