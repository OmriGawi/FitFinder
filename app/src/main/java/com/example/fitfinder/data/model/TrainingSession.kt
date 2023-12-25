package com.example.fitfinder.data.model

import com.google.firebase.Timestamp
import java.util.*

data class TrainingSession(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val sportCategory: String = "",
    val exercises: List<String> = emptyList(),
    val dateTime: Timestamp = Timestamp(Date()),
    val location: String = "",
    val additionalEquipment: String = "",
    val createdAt: Timestamp = Timestamp.now()
    // Future fields for tracking progress, etc.
    // Example: val progress: Map<String, Any> = mapOf()
)

