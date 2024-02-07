package com.example.fitfinder.data.repository.exercise

import android.util.Log
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

class TrainingSessionsRepository: BaseRepository() {
    private val db = FirebaseFirestore.getInstance()

    fun fetchTrainingSessions(userId: String, onSessionsFetched: (List<Pair<TrainingSession, Map<String, Any?>>>) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { userDocument ->
            val trainingSessionIds = userDocument.get("trainingSessions") as? List<String> ?: emptyList()

            val tasks = trainingSessionIds.map { sessionId ->
                val taskCompletionSource = TaskCompletionSource<Pair<TrainingSession, Map<String, Any?>>>()
                db.collection("training_sessions").document(sessionId).get()
                    .addOnSuccessListener { document ->
                        val trainingSession = document.toObject(TrainingSession::class.java)?.apply {
                            id = document.id // Set the document ID here
                        }
                        if (trainingSession != null) {
                            val partnerId = if (trainingSession.senderId == userId) trainingSession.receiverId else trainingSession.senderId
                            db.collection("users").document(partnerId).get()
                                .addOnSuccessListener { userSnapshot ->
                                    val partnerDetails = mutableMapOf<String, Any?>().apply {
                                        put("firstName", userSnapshot.getString("firstName"))
                                        put("lastName", userSnapshot.getString("lastName"))
                                    }
                                    taskCompletionSource.setResult(Pair(trainingSession, partnerDetails))
                                }
                                .addOnFailureListener { taskCompletionSource.setException(it) }
                        } else {
                            taskCompletionSource.setResult(null)
                        }
                    }
                    .addOnFailureListener { taskCompletionSource.setException(it) }
                taskCompletionSource.task
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener { trainingSessionTasks ->
                    val trainingSessionDetailsList = trainingSessionTasks.mapNotNull { task ->
                        if (task.isSuccessful && task.result != null) {
                            task.result as Pair<TrainingSession, Map<String, Any?>>
                        } else null
                    }.sortedByDescending { it.first.dateTime.toDate() }
                    onSessionsFetched(trainingSessionDetailsList)
                }
        }.addOnFailureListener { e ->
            Log.w("TrainingSessionRepository", "Error fetching training sessions: ", e)
        }
    }
}