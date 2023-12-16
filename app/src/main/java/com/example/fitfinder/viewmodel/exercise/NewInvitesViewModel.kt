package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository

class NewInvitesViewModel(private val repository: NewInvitesRepository) : ViewModel() {

    private val _invitesWithDetails = MutableLiveData<List<Pair<TrainingInvite, Map<String, Any?>>>>()
    val invitesWithDetails: LiveData<List<Pair<TrainingInvite, Map<String, Any?>>>> = _invitesWithDetails

    fun fetchNewInvites(userId: String) {
        repository.fetchNewInvites(userId) { newInvitesWithDetails ->
            _invitesWithDetails.postValue(newInvitesWithDetails)
        }
    }
}



