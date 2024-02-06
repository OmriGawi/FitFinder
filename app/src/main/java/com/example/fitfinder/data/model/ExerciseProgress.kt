package com.example.fitfinder.data.model

import java.util.*

data class ExerciseProgress(
    val exerciseName: String,
    var firstPerformance: String,
    var lastPerformance: String,
    var firstValue: Float = 0f, // Extracted numerical value for the first performance
    var lastValue: Float = 0f, // Extracted numerical value for the last performance
    var progressPercentage: Int = 0, // Calculated progress percentage
    var firstDate: Date = Date(Long.MAX_VALUE), // Date of the first performance
    var lastDate: Date = Date(0) // Date of the last performance
)


