package com.example.fitfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.data.repository.calendar.CalendarCreateEventRepository
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository
import com.example.fitfinder.data.repository.location.LocationRepository
import com.example.fitfinder.data.repository.messages.ChatRepository
import com.example.fitfinder.data.repository.messages.MatchesRepository
import com.example.fitfinder.data.repository.notification.NotificationRepository
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.data.repository.search.SearchRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.viewmodel.auth.ForgotPasswordViewModel
import com.example.fitfinder.viewmodel.auth.LoginViewModel
import com.example.fitfinder.viewmodel.auth.LogoutViewModel
import com.example.fitfinder.viewmodel.auth.SignupViewModel
import com.example.fitfinder.viewmodel.calendar.CalendarCreateEventViewModel
import com.example.fitfinder.viewmodel.exercise.NewInvitesViewModel
import com.example.fitfinder.viewmodel.location.LocationViewModel
import com.example.fitfinder.viewmodel.messages.ChatViewModel
import com.example.fitfinder.viewmodel.messages.MatchesViewModel
import com.example.fitfinder.viewmodel.notification.NotificationViewModel
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel
import com.example.fitfinder.viewmodel.search.SearchViewModel
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
        // Search
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(repository as SearchRepository) as T
        }
        // Notification
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(repository as NotificationRepository) as T
        }
        // Matches
        if (modelClass.isAssignableFrom(MatchesViewModel::class.java)) {
            return MatchesViewModel(repository as MatchesRepository) as T
        }
        // Chat
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository as ChatRepository) as T
        }
        // CalendarEvent
        if (modelClass.isAssignableFrom(CalendarCreateEventViewModel::class.java)) {
            return CalendarCreateEventViewModel(repository as CalendarCreateEventRepository) as T
        }
        // NewInvites
        if (modelClass.isAssignableFrom(NewInvitesViewModel::class.java)) {
            return NewInvitesViewModel(repository as NewInvitesRepository) as T
        }

        // Add more conditions for other ViewModel classes
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
