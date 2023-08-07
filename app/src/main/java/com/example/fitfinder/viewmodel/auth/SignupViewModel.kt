package com.example.fitfinder.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel class for managing the user registration logic in the app.
 * @property repository The authentication repository instance responsible for executing the registration operations.
 */
class SignupViewModel(private val repository: AuthRepository) : ViewModel() {

    val result = MutableLiveData<AuthRepository.RegistrationResult>()

    /**
     * Initiates a registration operation using the provided user details.
     *
     * @param fullName The complete name of the user (first name and last name combined).
     * @param birthDate The birth date of the user in a specific string format that can be converted to a Firestore Timestamp.
     * @param email The email address of the user.
     * @param password The password chosen by the user for their account.
     */
    fun register(fullName: String, birthDate: String, email: String, password: String) {
        // Convert the full name to first name and last name
        val (firstName, lastName) = convertFullNameToFirstLastName(fullName)

        // Convert the birthDate string to a Firestore Timestamp object
        val birthDateTimestamp = convertStringToTimestamp(birthDate)

        repository.register(firstName, lastName, birthDateTimestamp!!, email, password, result)
    }

    /**
     * Converts a date string in the format "dd/MM/yyyy" to a Firestore Timestamp object.
     *
     * @param dateString The date string to be converted.
     * @return A Firestore Timestamp object representing the date
     */
    private fun convertStringToTimestamp(dateString: String): Timestamp? {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.parse(dateString)?.let { Timestamp(it) }
    }

    /**
     * Converts a full name string to first name and last name parts.
     *
     * @param fullName The full name string to be converted.
     * @return A Pair containing the first name and last name parts of the full name.
     */
    private fun convertFullNameToFirstLastName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split(" ")
        val firstName = parts[0]
        val lastName = if (parts.size > 1) parts[1] else ""
        return Pair(firstName, lastName)
    }
}