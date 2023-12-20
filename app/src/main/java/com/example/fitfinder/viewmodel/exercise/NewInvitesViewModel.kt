package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository
import com.example.fitfinder.util.Event
import com.example.fitfinder.util.ToastyType

class NewInvitesViewModel(private val repository: NewInvitesRepository) : ViewModel() {

    private val _invitesWithDetails = MutableLiveData<List<Pair<TrainingInvite, Map<String, Any?>>>>()
    val invitesWithDetails: LiveData<List<Pair<TrainingInvite, Map<String, Any?>>>> = _invitesWithDetails

    private val _toastMessageEvent = MutableLiveData<Event<Pair<String, ToastyType>>>()
    val toastMessageEvent: LiveData<Event<Pair<String, ToastyType>>> = _toastMessageEvent

    fun fetchNewInvites(userId: String) {
        repository.fetchNewInvites(userId) { newInvitesWithDetails ->
            _invitesWithDetails.postValue(newInvitesWithDetails)
        }
    }

    fun declineInvite(inviteId: String, userId: String) {
        repository.declineInvite(inviteId, userId) { success ->
            if (success) {
                // Remove the declined invite from the LiveData list
                val updatedInvites = _invitesWithDetails.value?.filterNot { it.first.id == inviteId }
                _invitesWithDetails.postValue(updatedInvites)
                _toastMessageEvent.value = Event(Pair("Removed successfully!", ToastyType.SUCCESS))
            } else {
                _toastMessageEvent.value = Event(Pair("Failed to remove invite.", ToastyType.ERROR))
            }
        }
    }
}



