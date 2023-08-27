package com.example.fitfinder.data.model

enum class UserType {
    Trainer, Trainee;

    companion object {
        fun fromString(value: String): UserType? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
