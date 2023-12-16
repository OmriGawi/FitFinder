package com.example.fitfinder.data.repository.exercise

import android.util.Log
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.BaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewInvitesRepository : BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchNewInvites(userId: String, onInvitesReceived: (List<TrainingInvite>) -> Unit) {
        db.collection("invites")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "sent")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("NewInvitesRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val invitesList = snapshots?.documents?.mapNotNull { document ->
                    document.toObject(TrainingInvite::class.java)
                }.orEmpty()

                onInvitesReceived(invitesList)
            }
    }
}


