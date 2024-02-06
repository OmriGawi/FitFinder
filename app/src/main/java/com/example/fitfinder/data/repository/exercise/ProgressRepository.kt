package com.example.fitfinder.data.repository.exercise

import com.example.fitfinder.data.model.ExerciseProgress
import com.example.fitfinder.data.model.TrainingSessionReport
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProgressRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun fetchUserProgress(userId: String, onProgressFetched: (List<ExerciseProgress>) -> Unit) {
        // Get the references to the user's reports
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { userDocument ->
            val reportsRefs = userDocument.get("trainingSessionsReports") as? List<DocumentReference> ?: emptyList()

            // Fetch each report
            val tasks = reportsRefs.map { it.get() }
            Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener { documents ->
                val exerciseProgressList = processReports(documents)
                onProgressFetched(exerciseProgressList)
            }
        }.addOnFailureListener {
        }
    }

    private fun processReports(documents: List<DocumentSnapshot>): List<ExerciseProgress> {
        val exerciseMap = mutableMapOf<String, MutableList<Pair<Float, Date>>>()

        documents.forEach { document ->
            val report = document.toObject(TrainingSessionReport::class.java)
            report?.let {
                val reportDate = it.createdAt.toDate()

                it.reportDetails.forEach { (exercise, performance) ->
                    val (value, _) = extractPerformance(performance)
                    exerciseMap.getOrPut(exercise) { mutableListOf() }.add(value to reportDate)
                }
            }
        }

        // Convert the map into a list of ExerciseProgress, calculating first and last performance
        return exerciseMap.map { (exercise, performances) ->
            // Sort by date to find the first and last performance
            performances.sortBy { it.second }

            val firstPerformance = performances.first()
            val lastPerformance = performances.last()

            ExerciseProgress(
                exerciseName = exercise,
                firstPerformance = "${firstPerformance.first} ${firstPerformance.second}",
                lastPerformance = "${lastPerformance.first} ${lastPerformance.second}",
                firstValue = firstPerformance.first,
                lastValue = lastPerformance.first,
                progressPercentage = calculateProgressPercentage(firstPerformance.first, lastPerformance.first)
            )
        }
    }



    private fun extractPerformance(performance: String): Pair<Float, String> {
        // Extract the numerical value and the unit (e.g., "10 reps" -> 10f, "reps")
        // For simplicity, let's assume the value is always a float followed by a space and the unit
        val value = performance.split(" ")[0].toFloatOrNull() ?: 0f
        val unit = performance.split(" ").getOrNull(1) ?: ""
        return Pair(value, unit)
    }

    private fun calculateProgressPercentage(firstValue: Float, lastValue: Float): Int {
        if (firstValue == 0f) return 0 // Avoid division by zero
        return ((lastValue - firstValue) / firstValue * 100).toInt()
    }

}