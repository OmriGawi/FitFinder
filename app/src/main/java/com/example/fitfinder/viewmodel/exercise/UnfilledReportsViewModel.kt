package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.exercise.UnfilledReportsRepository

class UnfilledReportsViewModel(private val repository: UnfilledReportsRepository) : ViewModel() {
    private val _unfilledReportsWithDetails = MutableLiveData<List<Pair<TrainingSession, Map<String, Any?>>>>()
    val unfilledReportsWithDetails: LiveData<List<Pair<TrainingSession, Map<String, Any?>>>> = _unfilledReportsWithDetails

    fun fetchUnfilledReports(userId: String) {
        repository.fetchUnfilledReports(userId) { reportsWithDetails ->
            _unfilledReportsWithDetails.postValue(reportsWithDetails)
        }
    }
}


