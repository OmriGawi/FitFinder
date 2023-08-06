package com.example.fitfinder.data.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.fitfinder.data.model.User
import com.example.fitfinder.data.repository.BaseRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.*

class AuthRepository : BaseRepository() {
    private val auth = FirebaseAuth.getInstance()

    fun register(firstName: String, lastName: String, birthDate: Timestamp, email: String, password: String, result: MutableLiveData<RegistrationResult>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
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

    sealed class RegistrationResult {
        object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
        // Can add other specific errors here if needed.
    }

    sealed class LoginResult {
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
        // Can add other specific errors here if needed.
    }


}