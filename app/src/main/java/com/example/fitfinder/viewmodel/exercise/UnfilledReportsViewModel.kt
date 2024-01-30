package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.exercise.UnfilledReportsRepository
import com.example.fitfinder.util.Event
import com.example.fitfinder.util.ToastyType

class UnfilledReportsViewModel(private val repository: UnfilledReportsRepository) : ViewModel() {
    private val _unfilledReportsWithDetails = MutableLiveData<List<Pair<TrainingSession, Map<String, Any?>>>>()
    val unfilledReportsWithDetails: LiveData<List<Pair<TrainingSession, Map<String, Any?>>>> = _unfilledReportsWithDetails

    private val _selectedReport = MutableLiveData<Event<Pair<TrainingSession, Map<String, Any?>>>>()
    val selectedReport: LiveData<Event<Pair<TrainingSession, Map<String, Any?>>>> = _selectedReport

    private val _toastMessageEvent = MutableLiveData<Event<Pair<String, ToastyType>>>()
    val toastMessageEvent: LiveData<Event<Pair<String, ToastyType>>> get() = _toastMessageEvent

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

    fun submitReport(userId: String, trainingSession: TrainingSession, reportDetails: Map<String, String>) {
        repository.submitReport(userId, trainingSession, reportDetails) { success ->
            if (success) {
                _toastMessageEvent.postValue(Event(Pair("Report submitted successfully", ToastyType.SUCCESS)))
            } else {
                _toastMessageEvent.postValue(Event(Pair("Failed to submit", ToastyType.ERROR)))
            }
        }
    }



}


