package com.example.fitfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.viewmodel.auth.ForgotPasswordViewModel
import com.example.fitfinder.viewmodel.auth.LoginViewModel
import com.example.fitfinder.viewmodel.auth.LogoutViewModel
import com.example.fitfinder.viewmodel.auth.SignupViewModel

class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Signup
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(repository as AuthRepository) as T
        }
        // Login
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository as AuthRepository) as T
        }
        // LLogout
        if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
            return LogoutViewModel(repository as AuthRepository) as T
        }
        // Forgot Password
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(repository as AuthRepository) as T
        }

        // Add more conditions for other ViewModel classes
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
