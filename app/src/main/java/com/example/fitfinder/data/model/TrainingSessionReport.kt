package com.example.fitfinder.data.model

import com.google.firebase.Timestamp

data class TrainingSessionReport(
    val userId: String = "",
    val trainingSessionId: String = "",
    val reportDetails: Map<String, String> = mapOf(),
    val createdAt: Timestamp = Timestamp.now()
)

