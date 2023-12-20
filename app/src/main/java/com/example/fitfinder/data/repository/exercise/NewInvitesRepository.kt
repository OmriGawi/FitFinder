package com.example.fitfinder.data.repository.exercise

import android.content.ContentValues.TAG
import android.util.Log
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NewInvitesRepository : BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchNewInvites(userId: String, onInvitesReceived: (List<Pair<TrainingInvite, Map<String, Any?>>>) -> Unit) {
        db.collection("invites")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "sent")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val invitesWithDetails = snapshots?.documents?.mapNotNull { document ->
                    val invite = document.toObject(TrainingInvite::class.java)?.apply {
                        id = document.id // Set the document ID here
                    }
                    if (invite != null) {
                        val taskCompletionSource = TaskCompletionSource<Pair<TrainingInvite, Map<String, Any?>>>()
                        db.collection("users").document(invite.senderId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val userMap = mutableMapOf<String, Any?>().apply {
                                    put("firstName", userSnapshot.getString("firstName"))
                                    put("lastName", userSnapshot.getString("lastName"))
                                    put("profilePictureUrl", userSnapshot.get("userProfile.profilePictureUrl"))
                                }
                                taskCompletionSource.setResult(invite to userMap)
                            }
                            .addOnFailureListener { taskCompletionSource.setException(it) }
                        taskCompletionSource.task
                    } else null
                }

                Tasks.whenAllComplete(invitesWithDetails)
                    .addOnSuccessListener { inviteDetailTasks ->
                        val inviteDetailsList = inviteDetailTasks.mapNotNull { task ->
                            if (task.isSuccessful) {
                                task.result as Pair<TrainingInvite, Map<String, Any?>>
                            } else null
                        }
                        onInvitesReceived(inviteDetailsList)
                    }
            }
    }


    fun declineInvite(inviteId: String, userId: String, onComplete: (Boolean) -> Unit) {
        val inviteRef = db.collection("invites").document(inviteId)
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            // Get the current user document
            val userSnapshot = transaction.get(userRef)

            // Check if the user's invites array contains the inviteId
            val currentInvites = userSnapshot.get("invites") as? List<*>
            if (currentInvites != null && currentInvites.contains(inviteId)) {
                // Update the user's invites array by removing the inviteId
                transaction.update(userRef, "invites", FieldValue.arrayRemove(inviteId))
            }

            // Delete the invite document
            transaction.delete(inviteRef)
        }
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to decline invite: ", e)
                onComplete(false)
            }
    }
}


