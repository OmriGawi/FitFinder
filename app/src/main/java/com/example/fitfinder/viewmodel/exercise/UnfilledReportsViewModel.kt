package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.exercise.UnfilledReportsRepository
import com.example.fitfinder.util.Event

class UnfilledReportsViewModel(private val repository: UnfilledReportsRepository) : ViewModel() {
    private val _unfilledReportsWithDetails = MutableLiveData<List<Pair<TrainingSession, Map<String, Any?>>>>()
    val unfilledReportsWithDetails: LiveData<List<Pair<TrainingSession, Map<String, Any?>>>> = _unfilledReportsWithDetails

    private val _selectedReport = MutableLiveData<Event<Pair<TrainingSession, Map<String, Any?>>>>()
    val selectedReport: LiveData<Event<Pair<TrainingSession, Map<String, Any?>>>> = _selectedReport

    fun selectReport(trainingSession: TrainingSession, partnerDetails: Map<String, Any?>) {
        _selectedReport.value = Event(Pair(trainingSession, partnerDetails))
    }

    fun fetchUnfilledReports(userId: String) {
        repository.fetchUnfilledReports(userId) { reportsWithDetails ->
            _unfilledReportsWithDetails.postValue(reportsWithDetails)
        }
    }

    fun clearSelectedReport() {
        _selectedReport.value = null
    }


}


