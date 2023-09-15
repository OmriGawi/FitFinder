package com.example.fitfinder.data.repository.search

import com.example.fitfinder.data.model.*
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.util.Constants.EARTH_RADIUS_IN_KM
import com.example.fitfinder.util.ParsingUtil
import com.example.fitfinder.util.ParsingUtil.parseDistance
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


class SearchRepository : BaseRepository() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun searchPotentialUsers(userId: String, categoryName: String, skillLevel: String, times: List<String>, radius: Double): List<PotentialUser> {
        val currentUserLocation = fetchCurrentUserLocation(userId)

        val currentUserDocument = firestore.collection("users").document(userId).get().await()
        val acceptedUsers = currentUserDocument.get("accepted") as? List<String> ?: emptyList()
        val rejectedUsers = currentUserDocument.get("rejected") as? List<String> ?: emptyList()

        val usersMatchingWorkoutTimes = fetchUsersByWorkoutTimes(times, userId)

        val usersMatchingSportCategory = usersMatchingWorkoutTimes.filter { userDocument ->
            val userData = userDocument.data ?: return@filter false
            val potentialUserId = userDocument.id
            if (acceptedUsers.contains(potentialUserId) || rejectedUsers.contains(potentialUserId)) return@filter false
            userMatchesSportCategory(userData, categoryName, skillLevel)
        }

        // Use the modified usersWithinRadius
        val usersAndDistances = usersWithinRadius(usersMatchingSportCategory, currentUserLocation, radius)

        return usersAndDistances.mapNotNull { (userDocument, distance) ->
            val userData = userDocument.data ?: return@mapNotNull null
            mapDocumentToPotentialUser(userDocument.id, userData, distance)
        }
    }


    private suspend fun fetchCurrentUserLocation(userId: String): GeoPoint {
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        return documentSnapshot.getGeoPoint("location") ?: throw Exception("Location not found")
    }

    private suspend fun fetchUsersByWorkoutTimes(times: List<String>, currentUserId: String): List<DocumentSnapshot> {
        val results = mutableListOf<DocumentSnapshot>()

        for (time in times) {
            val snapshot = firestore.collection("users")
                .whereArrayContains("userProfile.workoutTimes", time)
                .whereNotEqualTo(FieldPath.documentId(), currentUserId)
                .get()
                .await()
                .documents
            results.addAll(snapshot)
        }

        // Filter out duplicates based on document IDs
        return results.distinctBy { it.id }
    }

    private fun userMatchesSportCategory(userData: Map<String, Any>, categoryName: String, skillLevel: String): Boolean {
        val userProfile = userData["userProfile"] as? Map<String, Any>
        val userCategories = userProfile?.get("sportCategories") as? List<Map<String, Any>>

        return userCategories?.any { category ->
            category["name"] == categoryName && category["skillLevel"] == skillLevel
        } ?: false
    }

    private fun usersWithinRadius(documents: List<DocumentSnapshot>, currentUserLocation: GeoPoint, radius: Double): List<Pair<DocumentSnapshot, Double>> {
        return documents.mapNotNull { document ->
            val userGeoPoint = document.getGeoPoint("location")
            if (userGeoPoint != null) {
                val distance = distanceBetween(currentUserLocation, userGeoPoint)
                if (distance <= radius) Pair(document, distance) else null
            } else null
        }
    }

    private fun distanceBetween(point1: GeoPoint, point2: GeoPoint): Double {
        val R = EARTH_RADIUS_IN_KM

        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }


    private fun mapDocumentToPotentialUser(documentId: String, data: Map<String, Any>, distance: Double): PotentialUser  {
        // Extract the userProfile map from data
        val userProfile = data["userProfile"] as? Map<String, Any> ?: emptyMap()

        return PotentialUser(
            userId = documentId,
            firstName = data["firstName"] as String,
            lastName = data["lastName"] as String,
            age = ParsingUtil.parseAge(data["birthDate"] as Timestamp),

            // Access fields from the userProfile map
            userType = UserType.valueOf(userProfile["userType"] as String),
            profilePictureUrl = userProfile["profilePictureUrl"] as? String,
            additionalPictures = (userProfile["additionalPictures"] as? List<String>) ?: emptyList(),
            sportCategories = (userProfile["sportCategories"] as? List<Map<String, Any>>)?.map {
                SportCategory(
                    name = it["name"] as String,
                    skillLevel = SkillLevel.valueOf(it["skillLevel"] as String)  // Assuming SkillLevel is an enum
                )
            } ?: emptyList(),
            workoutTimes = (userProfile["workoutTimes"] as? List<String>)?.map { WorkoutTime.valueOf(it) } ?: emptyList(),
            description = userProfile["description"] as? String,
            distance = parseDistance(distance)
        )
    }



    fun acceptUser(currentUserId: String, acceptedUserId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId)
        userRef.update("accepted", FieldValue.arrayUnion(acceptedUserId))
    }

    fun rejectUser(currentUserId: String, rejectedUserId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId)
        userRef.update("rejected", FieldValue.arrayUnion(rejectedUserId))
    }

}
