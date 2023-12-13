package com.example.fitfinder.data.repository.calendar

import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.BaseRepository
import com.google.firebase.firestore.FirebaseFirestore

class CalendarCreateEventRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun createInvite(invite: TrainingInvite, onSuccess: (Boolean) -> Unit) {
        db.collection("invites").add(invite)
            .addOnSuccessListener {
                onSuccess(true)
            }
            .addOnFailureListener {
                onSuccess(false)
            }
    }
}