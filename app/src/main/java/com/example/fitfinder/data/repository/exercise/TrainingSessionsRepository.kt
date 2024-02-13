package com.example.fitfinder.data.repository.exercise

import android.util.Log
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.Comparator

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
                    val now = Date() // Get current date and time
                    val trainingSessionDetailsList = trainingSessionTasks.mapNotNull { task ->
                        if (task.isSuccessful && task.result != null) {
                            task.result as Pair<TrainingSession, Map<String, Any?>>
                        } else null
                    }.sortedWith(Comparator { o1, o2 ->
                        val date1 = o1.first.dateTime.toDate()
                        val date2 = o2.first.dateTime.toDate()

                        when {
                            date1.before(now) && date2.before(now) -> date2.compareTo(date1) // Both before now, sort descending
                            date1.after(now) && date2.after(now) -> date1.compareTo(date2) // Both after now, sort ascending
                            else -> if (date1.before(now)) -1 else 1 // One is before now and one is after
                        }
                    })
                    onSessionsFetched(trainingSessionDetailsList)
                }
        }.addOnFailureListener { e ->
            Log.w("TrainingSessionRepository", "Error fetching training sessions: ", e)
        }
    }
}