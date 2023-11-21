package com.example.fitfinder.data.model

data class Message(
    val messageId: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val seen: Boolean
)
