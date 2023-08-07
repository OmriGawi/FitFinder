package com.example.fitfinder.data.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.fitfinder.data.model.User
import com.example.fitfinder.data.repository.BaseRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.*

/**
 * Class for authentication management, which contains methods for user registration and login.
 * Inherits from BaseRepository.
 */
class AuthRepository : BaseRepository() {

    // Instance of Firebase authentication.
    private val auth = FirebaseAuth.getInstance()

    /**
     * Registers a new user with the given details.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param birthDate The birth date of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param result A live data object to post the registration result.
     */
    fun register(firstName: String, lastName: String, birthDate: Timestamp, email: String, password: String, result: MutableLiveData<RegistrationResult>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = userId?.let { User(it, firstName, lastName, birthDate, email) }
                    userId?.let {
                        user?.let { it1 ->
                            FirebaseFirestore.getInstance().collection("users").document(it).set(it1)
                                .addOnCompleteListener { userCreationTask ->
                                    if (userCreationTask.isSuccessful) {
                                        result.value = RegistrationResult.Success
                                    } else {
                                        result.value = RegistrationResult.Error(userCreationTask.exception?.message ?: "Unknown Error")
                                    }
                                }
                        }
                    }
                } else {
                    result.value = RegistrationResult.Error(task.exception?.message ?: "Unknown Error")
                }
            }
    }


    /**
     * Logs in an existing user with the given credentials.
     * @param email The email of the user.
     * @param password The password of the user.
     * @param result A live data object to post the login result.
     */
    fun login(email: String, password: String, result: MutableLiveData<LoginResult>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = LoginResult.Success
                } else {
                    // Check the type or error code of the exception
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "User does not exist!"
                        is FirebaseAuthInvalidCredentialsException -> "Invalid credentials!"
                        // Add more specific exception types or error codes if needed
                        else -> task.exception?.message ?: "Login failed"
                    }
                    result.value = LoginResult.Error(errorMessage)
                }
            }
    }

    /**
     * Send password reset email link to specific email.
     * @param email The email of the user.
     * @param result A live data object to post the forgot password result.
     */
    fun sendPasswordResetEmail(email: String, result: MutableLiveData<ResetPasswordResult>) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = ResetPasswordResult.Success
                } else {
                    // Check the type or error code of the exception
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "User does not exist!"
                        // Can add more specific exception types or error codes if needed
                        else -> task.exception?.message ?: "Reset password failed"
                    }
                    result.value = ResetPasswordResult.Error(errorMessage)
                }
            }
    }


    /**
     * Sealed class representing the possible results of registration.
     */
    sealed class RegistrationResult {
        object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
        // Can add other specific errors here if needed.
    }

    /**
     * Sealed class representing the possible results of login.
     */
    sealed class LoginResult {
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
        // Can add other specific errors here if needed.
    }

    /**
     * Sealed class representing the possible results of reset password.
     */
    sealed class ResetPasswordResult {
        object Success : ResetPasswordResult()
        data class Error(val message: String) : ResetPasswordResult()
    }
}
