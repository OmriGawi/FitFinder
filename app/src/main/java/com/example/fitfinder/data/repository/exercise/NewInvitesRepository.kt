package com.example.fitfinder.data.repository.exercise

import android.util.Log
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
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

                // Assuming snapshots is not null here for brevity
                val tasks = snapshots!!.documents.map { document ->
                    val invite = document.toObject(TrainingInvite::class.java)
                    val taskCompletionSource = TaskCompletionSource<Pair<TrainingInvite, Map<String, Any?>>>()
                    invite?.let {
                        db.collection("users").document(invite.senderId).get()
                            .addOnSuccessListener { userSnapshot ->
                                val userMap = mutableMapOf<String, Any?>()
                                userMap["firstName"] = userSnapshot.getString("firstName")
                                userMap["lastName"] = userSnapshot.getString("lastName")
                                userMap["profilePictureUrl"] = userSnapshot.get("userProfile.profilePictureUrl")
                                taskCompletionSource.setResult(invite to userMap)
                            }
                            .addOnFailureListener { taskCompletionSource.setException(it) }
                    }
                    taskCompletionSource.task
                }

                Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener { inviteDetailTasks ->
                        val inviteDetailsList = inviteDetailTasks.mapNotNull { task ->
                            if (task.isSuccessful) {
                                val pair = task.result as Pair<TrainingInvite, Map<String, Any?>>
                                pair
                            } else null
                        }
                        onInvitesReceived(inviteDetailsList)
                    }
            }
    }
}


