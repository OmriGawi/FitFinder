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

            // Listen for real-time updates
            userRef.addSnapshotListener { userSnapshot, e ->
                if (e != null) {
                    Log.w("CalendarRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (userSnapshot != null && userSnapshot.exists()) {
                    val trainingSessionIds = userSnapshot.get("trainingSessions") as? List<String> ?: emptyList()
                    val trainingSessionTasks = trainingSessionIds.map { sessionId ->
                        db.collection("training_sessions").document(sessionId).get()
                    }

                    // Using Tasks API to wait for all the fetches to complete
                    Tasks.whenAllSuccess<DocumentSnapshot>(trainingSessionTasks).addOnSuccessListener { documents ->
                        val eventsList = documents.mapNotNull { it.toObject(TrainingSession::class.java) }
                        onEventsReceived(eventsList)
                    }
                } else {
                    Log.d("CalendarRepository", "Current user data: null")
                }
            }
        }


    }