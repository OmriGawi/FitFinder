package com.example.fitfinder.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.*
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.util.Event
import com.example.fitfinder.util.ToastyType

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    // Variables
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _toastMessageEvent = MutableLiveData<Event<Pair<String, ToastyType>>>()
    val toastMessageEvent: LiveData<Event<Pair<String, ToastyType>>> get() = _toastMessageEvent

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchUserProfile(userId: String) {
        repository.getUserProfile(userId).addOnSuccessListener { document ->
            val userProfileMap = document.get("userProfile") as? MutableMap<String, Any>

            if (userProfileMap?.get("profilePictureUrl") == "") {
                // If Firestore does not have the URL, fetch default one from Firebase Storage
                repository.getDefaultProfilePictureUrl().addOnSuccessListener { defaultUrl ->
                    userProfileMap["profilePictureUrl"] = defaultUrl.toString()
                    createUserProfile(userProfileMap, userId)
                }
            } else {
                createUserProfile(userProfileMap, userId)
            }
        }
    }

    private fun createUserProfile(userProfileMap: MutableMap<String, Any>?, userId: String) {
        val profile = userProfileMap?.let {
            UserProfile(
                userId = userId,
                userType = UserType.valueOf(it["userType"] as? String ?: ""),
                profilePictureUrl = it["profilePictureUrl"] as? String,
                additionalPictures = (it["additionalPictures"] as? MutableList<String>) ?: mutableListOf(),
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
        _userProfile.postValue(profile!!)
    }

    fun updateProfilePictureUrl(userId: String, uri: Uri) {
        _isLoading.postValue(true)  // Show the ProgressBar

        repository.uploadProfilePicture(userId, uri).addOnSuccessListener { downloadUri ->
            // After successful upload, update the userProfile with the new URL
            val currentProfile = _userProfile.value
            currentProfile?.profilePictureUrl = downloadUri.toString()
            _userProfile.postValue(currentProfile!!)
            _isLoading.postValue(false)  // Hide the ProgressBar
            _toastMessageEvent.postValue(Event(Pair("Image uploaded successfully!", ToastyType.SUCCESS)))
        }.addOnFailureListener {
            // Notify the user of the error
            _toastMessageEvent.postValue(Event(Pair("Failed to upload the image.", ToastyType.ERROR)))
            _isLoading.postValue(false)  // Hide the ProgressBar
        }
    }

    fun addAdditionalPicture(userId: String, uri: Uri) {
        repository.uploadAdditionalPicture(userId, uri).addOnSuccessListener { downloadUri ->
            val currentProfile = _userProfile.value
            currentProfile?.additionalPictures?.add(downloadUri.toString()) // 0 : to the beginning of the list
            _userProfile.postValue(currentProfile!!)
            _toastMessageEvent.postValue(Event(Pair("Additional Image uploaded successfully!", ToastyType.SUCCESS)))
        }.addOnFailureListener {
            // Notify the user of the error
            _toastMessageEvent.postValue(Event(Pair("Failed to upload the additional image.", ToastyType.ERROR)))
        }
    }

    fun addSportCategory(sportCategory: SportCategory) {
        val currentProfile = _userProfile.value
        currentProfile?.sportCategories?.add(0, sportCategory)  // 0 : to the beginning of the list
        _userProfile.postValue(currentProfile!!)
    }

    fun updateUserProfile(userId: String, userProfile: UserProfile) {
        repository.updateUserProfile(userId, userProfile)
    }
}
