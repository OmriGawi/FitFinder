package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository

class NewInvitesViewModel(private val repository: NewInvitesRepository) : ViewModel() {

    private val _invites = MutableLiveData<List<TrainingInvite>>()
    val invites: LiveData<List<TrainingInvite>> = _invites

    fun fetchNewInvites(userId: String) {
        repository.fetchNewInvites(userId) { newInvites ->
            _invites.postValue(newInvites)
        }
    }
}


