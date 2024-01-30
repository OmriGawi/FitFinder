package com.example.fitfinder.data.repository.exercise

import android.util.Log
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.model.TrainingSessionReport
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UnfilledReportsRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchUnfilledReports(userId: String, onReportsFetched: (List<Pair<TrainingSession, Map<String, Any?>>>) -> Unit) {
        val userRef = db.collection("users").document(userId)
        val currentDate = Calendar.getInstance().time

        userRef.get().addOnSuccessListener { userDocument ->
            val trainingSessionIds = userDocument.get("trainingSessions") as? List<String> ?: emptyList()
            val tasks = trainingSessionIds.map { sessionId ->
                val taskCompletionSource = TaskCompletionSource<Pair<TrainingSession, Map<String, Any?>>>()
                db.collection("training_sessions").document(sessionId).get()
                    .addOnSuccessListener { document ->
                        val trainingSession = document.toObject(TrainingSession::class.java)?.apply {
                            id = document.id // Set the document ID here
                        }
                        if (trainingSession != null &&
                            trainingSession.dateTime.toDate().before(currentDate) &&
                            trainingSession.reportStatus[userId] == false) {
                            // Determine the partner's userId
                            val partnerId = if (trainingSession.senderId == userId) trainingSession.receiverId else trainingSession.senderId

                            // Fetch the partner's user details
                            db.collection("users").document(partnerId).get()
                                .addOnSuccessListener { userSnapshot ->
                                    val userMap = mutableMapOf<String, Any?>().apply {
                                        put("firstName", userSnapshot.getString("firstName"))
                                        put("lastName", userSnapshot.getString("lastName"))
                                        put("profilePictureUrl", userSnapshot.get("userProfile.profilePictureUrl"))
                                    }
                                    taskCompletionSource.setResult(trainingSession to userMap)
                                }
                                .addOnFailureListener { taskCompletionSource.setException(it) }
                        } else {
                            taskCompletionSource.setException(Exception("TrainingSession not found"))
                        }
                    }
                    .addOnFailureListener { taskCompletionSource.setException(it) }
                taskCompletionSource.task
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener { trainingSessionTasks ->
                    val trainingSessionDetailsList = trainingSessionTasks.mapNotNull { task ->
                        if (task.isSuccessful) {
                            task.result as Pair<TrainingSession, Map<String, Any?>>
                        } else null
                    }.sortedByDescending { it.first.dateTime.toDate() }
                    onReportsFetched(trainingSessionDetailsList)
                }
        }.addOnFailureListener { e ->
            Log.w("UnfilledReportsRepository", "Error fetching training sessions: ", e)
        }
    }

    fun submitReport(userId: String, trainingSession: TrainingSession, reportDetails: Map<String, String>, onComplete: (Boolean) -> Unit) {

        val report = TrainingSessionReport(
            userId = userId,
            trainingSessionId = trainingSession.id,
            reportDetails = reportDetails,
            createdAt = trainingSession.dateTime // Use the session dateTime as the createdAt timestamp
        )

        // Reference to the training_sessions_reports collection
        val reportRef = db.collection("training_sessions_reports").document()

        // Start a batch operation to ensure atomicity
        val batch = db.batch()

        // Add the report to the batch
        batch.set(reportRef, report)

        // Update the reportStatus in the training session
        val reportStatusUpdate = trainingSession.reportStatus.toMutableMap()
        reportStatusUpdate[userId] = true
        batch.update(db.collection("training_sessions").document(trainingSession.id), "reportStatus", reportStatusUpdate)

        // Add the report reference to the user's document
        batch.update(db.collection("users").document(userId), "trainingSessionsReports", FieldValue.arrayUnion(reportRef))

        // Commit the batch operation
        batch.commit().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }



}
