package com.example.fitfinder.data.repository.exercise

import android.content.ContentValues.TAG
import android.util.Log
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.model.TrainingSession
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

    fun acceptInvite(invite: TrainingInvite, userId: String, onComplete: (Boolean) -> Unit) {
        val inviteRef = db.collection("invites").document(invite.id)
        val receiverRef = db.collection("users").document(userId)
        val senderRef = db.collection("users").document(invite.senderId)
        val trainingSessionRef = db.collection("training_sessions").document()

        db.runTransaction { transaction ->
            // Remove the invite from the receiver's invites array
            transaction.update(receiverRef, "invites", FieldValue.arrayRemove(invite.id))

            // Delete the invite document
            transaction.delete(inviteRef)

            // Create a new document in training_sessions
            val trainingSession = TrainingSession(
                id = trainingSessionRef.id,
                senderId = invite.senderId,
                receiverId = invite.receiverId,
                sportCategory = invite.sportCategory,
                exercises = invite.exercises,
                dateTime = invite.dateTime,
                location = invite.location,
                additionalEquipment = invite.additionalEquipment,
                createdAt = invite.createdAt,
                reportStatus = mapOf(invite.senderId to false, invite.receiverId to false)
                // Additional fields can be added as needed
            )
            transaction.set(trainingSessionRef, trainingSession)

            // Add the reference to the new training session in both users' documents
            transaction.update(receiverRef, "trainingSessions", FieldValue.arrayUnion(trainingSessionRef.id))
            transaction.update(senderRef, "trainingSessions", FieldValue.arrayUnion(trainingSessionRef.id))
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }
}


