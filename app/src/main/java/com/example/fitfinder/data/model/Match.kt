package com.example.fitfinder.data.model

import com.google.firebase.Timestamp

data class Match(
    val matchId: String = "",
    val user1: String = "",
    val user2: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val lastMessage: Message? = null
)


