package com.example.fitfinder.data.repository.search

import com.example.fitfinder.data.model.PotentialUser
import com.example.fitfinder.data.repository.BaseRepository
import com.example.fitfinder.util.ParsingUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery

class SearchRepository : BaseRepository() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersRef = firestore.collection("users")
    private val geoFirestore = GeoFirestore(usersRef)

    suspend fun searchPotentialUsers(userId: String, categoryName: String, skillLevel: String, times: List<String>, radius: Double): List<PotentialUser> {
        val potentialUsers = mutableListOf<PotentialUser>()

        try {
            val currentUserGeoPoint = fetchCurrentUserLocation(userId)
            val keysWithinRadius = fetchKeysWithinRadius(currentUserGeoPoint, radius)
            val querySnapshot = fetchPotentialUsers(keysWithinRadius, times)

            for (document in querySnapshot.documents) {
                val data = document.data ?: continue
                val potentialUser = mapDocumentToPotentialUser(document.id, data)
                potentialUsers.add(potentialUser)
            }
        } catch (e: Exception) {
            throw e
        }

        return potentialUsers
    }

    private suspend fun fetchCurrentUserLocation(userId: String): GeoPoint {
        val documentSnapshot = firestore.collection("users").document(userId).get().await()
        return documentSnapshot.getGeoPoint("location") ?: throw Exception("Location not found")
    }

    private suspend fun fetchKeysWithinRadius(geoPoint: GeoPoint, radius: Double): List<String> {
        val geoQuery: GeoQuery = geoFirestore.queryAtLocation(geoPoint, radius)
        val geoQuerySnapshot = geoQuery.get().await() // <-- Make sure get() exists on GeoQuery
        return geoQuerySnapshot.map { snapshot -> snapshot.id } // <-- Explicit parameter name
    }

    private suspend fun fetchPotentialUsers(keysWithinRadius: List<String>, times: List<String>) =
        firestore.collection("users")
            .whereIn("userProfile.workoutTimes", times)
            .whereIn(FieldPath.documentId(), keysWithinRadius)
            .get()
            .await()

    private fun mapDocumentToPotentialUser(documentId: String, data: Map<String, Any>): PotentialUser {
        return PotentialUser(
            userId = documentId,
            firstName = data["firstName"] as String,
            lastName = data["lastName"] as String,
            age = ParsingUtil.parseAge(data["birthDate"] as Timestamp),
            profilePictureUrl = "someUrl" // <-- Provide the required value
        )
    }
}
