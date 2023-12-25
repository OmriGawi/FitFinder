package com.example.fitfinder.data.repository.calendar

import android.util.Log
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CalendarRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchCalendarEvents(userId: String, onEventsReceived: (List<TrainingSession>) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { userDocument ->
            val trainingSessionIds =
                userDocument.get("trainingSessions") as? List<String> ?: emptyList()
            val tasks = trainingSessionIds.map { sessionId ->
                db.collection("training_sessions").document(sessionId).get()
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener { documents ->
                val eventsList = documents.mapNotNull { it.toObject(TrainingSession::class.java) }
                onEventsReceived(eventsList)
            }
        }.addOnFailureListener { e ->
            Log.w("CalendarEventsRepository", "Error fetching training sessions: ", e)
        }

    }


}