package com.example.fitfinder.util

import com.google.firebase.Timestamp
import java.util.*

object ParsingUtil {

    fun parseRadius(radiusStr: String): Double {
        return radiusStr.replace(" km", "").toDoubleOrNull() ?: Double.MAX_VALUE // Replace "More than 100 km" with a large number or other fallback value
    }

    fun parseAge(birthDate: Timestamp): Int {
        val birthDateCalendar = Calendar.getInstance()
        birthDateCalendar.time = birthDate.toDate()

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthDateCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDateCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun parseDistance(distance: Double): Float {
        val roundedDistance = if (distance < 1) 1.0 else distance
        return String.format("%.2f", roundedDistance).toFloat()
    }

}
