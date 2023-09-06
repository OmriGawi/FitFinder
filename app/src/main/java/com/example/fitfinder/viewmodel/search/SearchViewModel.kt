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

    fun searchPotentialUsers(userId: String, sportCategory: SportCategory, workoutTimes: List<WorkoutTime>, radius: String) {
        // Set loading to true
        _isLoading.value = true

        val categoryName = sportCategory.name
        val skillLevel = sportCategory.skillLevel.name
        val times = workoutTimes.map { it.name }

        val numericalRadius = ParsingUtil.parseRadius(radius)

        viewModelScope.launch {
            try {
                val users = repository.searchPotentialUsers(userId, categoryName, skillLevel, times, numericalRadius)
                _potentialUsers.value = users

                // Set loading to false
                _isLoading.value = false
            } catch (e: Exception) {
                // Handle the error
                _toastMessageEvent.value = Event(Pair("An error occurred: ${e.message}", ToastyType.ERROR))

                // Set loading to false
                _isLoading.value = false
            }
        }
    }

}