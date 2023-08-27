package com.example.fitfinder.data.model

enum class WorkoutTime {
    Morning, Afternoon, Evening;

    companion object {
        fun fromString(value: String): WorkoutTime? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
