package com.example.fitfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.data.repository.location.LocationRepository
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.viewmodel.auth.ForgotPasswordViewModel
import com.example.fitfinder.viewmodel.auth.LoginViewModel
import com.example.fitfinder.viewmodel.auth.LogoutViewModel
import com.example.fitfinder.viewmodel.auth.SignupViewModel
import com.example.fitfinder.viewmodel.location.LocationViewModel
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel

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
        // Logout
        if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
            return LogoutViewModel(repository as AuthRepository) as T
        }
        // Forgot Password
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(repository as AuthRepository) as T
        }
        // UserProfile
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(repository as UserProfileRepository) as T
        }
        // Sport Categories
        if (modelClass.isAssignableFrom(SportCategoriesViewModel::class.java)) {
            return SportCategoriesViewModel(repository as SportCategoriesRepository) as T
        }
        // Location
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(repository as LocationRepository) as T
        }

        // Add more conditions for other ViewModel classes
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
