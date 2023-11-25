package com.example.fitfinder.viewmodel.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.Match
import com.example.fitfinder.data.model.PotentialUser
import com.example.fitfinder.data.repository.messages.MatchesRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

class MatchesViewModel(private val repository: MatchesRepository) : ViewModel() {

    // LiveData for matches and corresponding users
    val matchesWithUsers = MutableLiveData<List<Pair<Match, PotentialUser>>>()

    // LiveData for the currently selected match and user
    private val _selectedMatchWithUser = MutableLiveData<Pair<Match, PotentialUser>>()
    val selectedMatchWithUser: LiveData<Pair<Match, PotentialUser>> = _selectedMatchWithUser

    // Fetch matches and corresponding user details for the given user ID
    fun fetchMatchesAndUsersForUser(userId: String) {
        repository.fetchMatchesAndUsersForUser(userId).addOnSuccessListener { matchesAndUsersList ->
            matchesWithUsers.value = matchesAndUsersList
        }.addOnFailureListener {
            // Handle the error, maybe update LiveData to show an error message
        }
    }

    // Listen for real-time updates to the user's matches
    fun observeMatchesUpdates(userId: String) {
        repository.setMatchesUpdateListener(userId, EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                // Handle the error, maybe update LiveData to show an error message
                return@EventListener
            }

            if (snapshot != null && snapshot.exists()) {
                fetchMatchesAndUsersForUser(userId) // Refetch the matches and users when there's an update
            }
        })
    }

    // Function to set the currently selected match and user
    fun selectMatchWithUser(match: Match, potentialUser: PotentialUser) {
        _selectedMatchWithUser.value = Pair(match, potentialUser)
    }
}
