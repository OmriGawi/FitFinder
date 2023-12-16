package com.example.fitfinder.data.model
import com.google.firebase.Timestamp
import java.util.*

data class TrainingInvite(
    val senderId: String = "",
    val receiverId: String = "",
    val sportCategory: String = "",
    val exercises: List<String> = emptyList(),
    val dateTime: Timestamp = Timestamp(Date()),
    val location: String = "",
    val additionalEquipment: String = "",
    var status: String = "sent",
    val createdAt: Timestamp = Timestamp.now()
)


