package com.example.fitfinder.data.repository.messages

import com.example.fitfinder.data.model.*
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.util.ParsingUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore

class MatchesRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val matchesCollection = db.collection("matches")

    fun fetchMatchesAndUsersForUser(userId: String): Task<List<Pair<Match, PotentialUser>>> {
        val source = TaskCompletionSource<List<Pair<Match, PotentialUser>>>()

        usersCollection.document(userId).get().addOnSuccessListener { userDocument ->
            val matchIds = userDocument["matches"] as? List<String> ?: listOf()

            val matchTasks = matchIds.map { matchId ->
                matchesCollection.document(matchId).get()
            }

            Tasks.whenAllSuccess<DocumentSnapshot>(matchTasks).addOnSuccessListener { matchDocuments ->
                val matchList = matchDocuments.mapNotNull { document ->
                    val match = document.toObject(Match::class.java)
                    match?.copy(matchId = document.id)
                }

                // Fetch the potential user for each match
                val userTasks = matchList.map { match ->
                    val otherUserId = if (match.user1 == userId) match.user2 else match.user1
                    usersCollection.document(otherUserId).get()
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(userTasks).addOnSuccessListener { userDocuments ->
                    val userList = userDocuments.mapNotNull { document ->
                        mapDocumentToPotentialUser(document.id, document.data!!)
                    }
                    val resultPairs = matchList.zip(userList)
                    source.setResult(resultPairs)
                }.addOnFailureListener { exception ->
                    source.setException(exception)
                }

            }.addOnFailureListener { exception ->
                source.setException(exception)
            }
        }.addOnFailureListener { exception ->
            source.setException(exception)
        }

        return source.task
    }

    fun setMatchesUpdateListener(userId: String, listener: EventListener<DocumentSnapshot>) {
        usersCollection.document(userId).addSnapshotListener(listener)
    }

    // This function is derived from your search repository
    private fun mapDocumentToPotentialUser(documentId: String, data: Map<String, Any>): PotentialUser {
        val userProfile = data["userProfile"] as? Map<String, Any> ?: emptyMap()

        return PotentialUser(
            userId = documentId,
            firstName = data["firstName"] as String,
            lastName = data["lastName"] as String,
            age = ParsingUtil.parseAge(data["birthDate"] as Timestamp),

            userType = UserType.valueOf(userProfile["userType"] as String),
            profilePictureUrl = userProfile["profilePictureUrl"] as? String,
            additionalPictures = (userProfile["additionalPictures"] as? List<String>) ?: emptyList(),
            sportCategories = (userProfile["sportCategories"] as? List<Map<String, Any>>)?.map {
                SportCategory(
                    name = it["name"] as String,
                    skillLevel = SkillLevel.valueOf(it["skillLevel"] as String)
                )
            } ?: emptyList(),
            workoutTimes = (userProfile["workoutTimes"] as? List<String>)?.map { WorkoutTime.valueOf(it) } ?: emptyList(),
            description = userProfile["description"] as? String,
            distance = null  // You may need to adjust this if needed
        )
    }
}



