package com.example.fitfinder.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.auth.AuthRepository

/**
 * ViewModel class for managing the logout logic in the app.
 * @property repository The authentication repository instance responsible for executing the authentication operations.
 */
class LogoutViewModel(private val repository: AuthRepository) : ViewModel() {

    val result = MutableLiveData<AuthRepository.LogoutResult>()

    /**
     * Initiates a logout operation.
     */
    fun logout() {
        repository.logout(result)
    }

}