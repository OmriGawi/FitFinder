package com.example.fitfinder.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.auth.AuthRepository

/**
 * ViewModel class for managing the forgot password logic in the app.
 * @property repository The authentication repository instance responsible for executing the authentication operations.
 */
class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    val resetPasswordResult = MutableLiveData<AuthRepository.ResetPasswordResult>()

    /**
     * Initiates a forgot password operation using the provided email.
     * @param email The email address used for login.
     */
    fun sendPasswordResetEmail(email: String) {
        repository.sendPasswordResetEmail(email, resetPasswordResult)
    }
}
