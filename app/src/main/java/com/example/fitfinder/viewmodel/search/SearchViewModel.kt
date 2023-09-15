package com.example.fitfinder.viewmodel.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfinder.data.model.PotentialUser
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.search.SearchRepository
import com.example.fitfinder.util.Event
import com.example.fitfinder.util.ParsingUtil
import com.example.fitfinder.util.ToastyType
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    // Variables
    private val _potentialUsers = MutableLiveData<List<PotentialUser>>()
    val potentialUsers: LiveData<List<PotentialUser>> get() = _potentialUsers

    private val _toastMessageEvent = MutableLiveData<Event<Pair<String, ToastyType>>>()
    val toastMessageEvent: LiveData<Event<Pair<String, ToastyType>>> get() = _toastMessageEvent

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val navigateToPotentialUsers = MutableLiveData(false)


    fun searchPotentialUsers(userId: String, sportCategory: SportCategory, workoutTimes: List<WorkoutTime>, radius: String) {
        // Set loading to true
        _isLoading.value = true

        val categoryName = sportCategory.name
        val skillLevel = sportCategory.skillLevel.name
        val times = workoutTimes.map { it.name }

        val numericalRadius = ParsingUtil.parseRadius(radius)

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val users = repository.searchPotentialUsers(userId, categoryName, skillLevel, times, numericalRadius)
                _isLoading.value = false

                if (users.isEmpty()) {
                    _toastMessageEvent.value =
                        Event(Pair("No potential users found.", ToastyType.INFO))
                }else{
                    _potentialUsers.value = users
                    navigateToPotentialUsers.value = true
                }
            } catch (e: Exception) {
                _toastMessageEvent.value = Event(Pair("Error: ${e.message}", ToastyType.ERROR))
                _isLoading.value = false
            }
        }
    }

    fun acceptUser(currentUserId: String, acceptedUserId: String) {
        viewModelScope.launch {
            try {
                repository.acceptUser(currentUserId, acceptedUserId)
                // Maybe trigger a LiveData event if you want to give feedback
            } catch (e: Exception) {
                _toastMessageEvent.value = Event(Pair("Error: ${e.message}", ToastyType.ERROR))
            }
        }
    }

    fun rejectUser(currentUserId: String, rejectedUserId: String) {
        viewModelScope.launch {
            try {
                repository.rejectUser(currentUserId, rejectedUserId)
                // Maybe trigger a LiveData event if you want to give feedback
            } catch (e: Exception) {
                _toastMessageEvent.value = Event(Pair("Error: ${e.message}", ToastyType.ERROR))
            }
        }
    }

    fun setNavigateToPotentialUsers(condition: Boolean){
        navigateToPotentialUsers.value = condition
    }

}