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
            var userProfileMap = document.get("userProfile") as? MutableMap<String, Any>

            // If userProfileMap is null (i.e., no user profile exists) or the profilePictureUrl field is missing or empty
            if (userProfileMap == null || userProfileMap["profilePictureUrl"] == "") {
                // Fetch default profile picture URL from Firebase Storage
                repository.getDefaultProfilePictureUrl().addOnSuccessListener { defaultUrl ->
                    if (userProfileMap == null) {
                        userProfileMap = mutableMapOf()
                    }
                    userProfileMap!!["profilePictureUrl"] = defaultUrl.toString()
                    createUserProfile(userProfileMap, userId)
                }
            } else {
                createUserProfile(userProfileMap, userId)
            }
        }
    }


    private fun createUserProfile(userProfileMap: MutableMap<String, Any>?, userId: String) {
        val profile = UserProfile(
            userId = userId,
            userType = UserType.fromString(userProfileMap?.get("userType") as? String ?: "") ?: UserType.Trainee, // default to Trainee if not found
            profilePictureUrl = userProfileMap?.get("profilePictureUrl") as? String ?: "",
            additionalPictures = userProfileMap?.get("additionalPictures") as? MutableList<String> ?: mutableListOf(),
            sportCategories = (userProfileMap?.get("sportCategories") as? List<Map<String, String>>)?.map { categoryMap ->
                SportCategory(
                    name = categoryMap["name"] ?: "",
                    skillLevel = SkillLevel.valueOf(categoryMap["skillLevel"] ?: "")
                )
            } ?.toMutableList() ?: mutableListOf(),
            workoutTimes = (userProfileMap?.get("workoutTimes") as? List<String>)?.mapNotNull { WorkoutTime.fromString(it) }?.toMutableList() ?: mutableListOf(),
            description = userProfileMap?.get("description") as? String ?: ""
        )
        _userProfile.postValue(profile)
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

    fun updateUserType(userType: UserType) {
        val currentProfile = _userProfile.value
        currentProfile?.userType = userType
        _userProfile.postValue(currentProfile!!)
    }

    fun updateWorkoutTimes(updatedWorkoutTimes: MutableList<WorkoutTime>) {
        val currentProfile = _userProfile.value
        currentProfile?.workoutTimes?.clear()
        currentProfile?.workoutTimes?.addAll(updatedWorkoutTimes)
        _userProfile.postValue(currentProfile!!)
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

    fun removeAdditionalPicture(imageUrl: String) {
        val currentProfile = _userProfile.value
        currentProfile?.additionalPictures?.remove(imageUrl)
        _userProfile.postValue(currentProfile!!)
    }

    fun addSportCategory(sportCategory: SportCategory) {
        val currentProfile = _userProfile.value
        currentProfile?.sportCategories?.add(0, sportCategory)  // 0 : to the beginning of the list
        _userProfile.postValue(currentProfile!!)
    }

    fun removeSportCategory(sportCategory: SportCategory) {
        val currentProfile = _userProfile.value
        currentProfile?.sportCategories?.remove(sportCategory)
        _userProfile.postValue(currentProfile!!)
    }

    fun updateUserDescription(description: String) {
        val currentProfile = _userProfile.value
        currentProfile?.description = description
        _userProfile.postValue(currentProfile!!)
    }

    fun updateUserProfile(userId: String) {
        _userProfile.value?.let {
            repository.updateUserProfile(userId, it)
                .addOnSuccessListener {
                    _toastMessageEvent.postValue(Event(Pair("Profile updated successfully!", ToastyType.SUCCESS)))
                }
                .addOnFailureListener {
                    _toastMessageEvent.postValue(Event(Pair("Failed to update profile.", ToastyType.ERROR)))
                }
        }
    }


}
