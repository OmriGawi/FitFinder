package com.example.fitfinder.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.auth.AuthRepository

/**
 * ViewModel class for managing the login logic in the app.
 * @property repository The authentication repository instance responsible for executing the authentication operations.
 */
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    val result = MutableLiveData<AuthRepository.LoginResult>()

    /**
     * Initiates a login operation using the provided email and password.
     * @param email The email address used for login.
     * @param password The password associated with the email for login.
     */
    fun login(email: String, password: String) {
        repository.login(email, password, result)
    }
}