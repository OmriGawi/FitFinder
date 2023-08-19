package com.example.fitfinder.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.*
import com.example.fitfinder.data.repository.profile.UserProfileRepository

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    fun fetchUserProfile(userId: String) {
        repository.getUserProfile(userId).addOnSuccessListener { document ->
            val userProfileMap = document.get("userProfile") as? Map<String, Any>
            val profile = userProfileMap?.let {
                UserProfile(
                    userId = userId,
                    userType = UserType.valueOf(it["userType"] as? String ?: ""),
                    profilePictureUrl = it["profilePictureUrl"] as? String,
                    additionalPictures = (it["additionalPictures"] as? List<String>) ?: listOf(),
                    sportCategories = (it["sportCategories"] as? List<Map<String, String>>)?.map { categoryMap ->
                        SportCategory(
                            name = categoryMap["name"] ?: "",
                            skillLevel = SkillLevel.valueOf(categoryMap["skillLevel"] ?: "")
                        )
                    } ?.toMutableList() ?: mutableListOf(),
                    workoutTimes = (it["workoutTimes"] as? List<String>)?.map { WorkoutTime.valueOf(it) } ?: listOf(),
                    description = it["description"] as? String
                )
            }
            _userProfile.postValue(profile)
        }
    }

    fun addSportCategory(sportCategory: SportCategory) {
        val currentProfile = _userProfile.value
        currentProfile?.sportCategories?.add(0, sportCategory)  // 0 : to the beginning of the list
        _userProfile.postValue(currentProfile)
    }

    fun updateUserProfile(userId: String, userProfile: UserProfile) {
        repository.updateUserProfile(userId, userProfile)
    }

}
