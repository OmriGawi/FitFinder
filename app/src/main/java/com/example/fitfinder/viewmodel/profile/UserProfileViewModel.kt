package com.example.fitfinder.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.UserProfile
import com.example.fitfinder.data.repository.profile.UserProfileRepository

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    fun fetchUserProfile(userId: String) {
        repository.getUserProfile(userId).addOnSuccessListener { document ->
            val profile = document.toObject(UserProfile::class.java)
            _userProfile.postValue(profile)
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        repository.updateUserProfile(userProfile)
    }

}
