package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.exercise.TrainingSessionsRepository
import java.util.*

class TrainingSessionsViewModel(private val repository: TrainingSessionsRepository) : ViewModel() {

    private val _allTrainingSessions = MutableLiveData<List<Pair<TrainingSession, Map<String, Any?>>>>()
    val filteredTrainingSessions = MutableLiveData<List<Pair<TrainingSession, Map<String, Any?>>>>()

    fun fetchTrainingSessions(userId: String) {
        repository.fetchTrainingSessions(userId) { sessions ->
            _allTrainingSessions.value = sessions
            // Immediately apply "Future" filter upon data fetch
            filterSessions("Future")
        }
    }

    fun filterSessions(filter: String) {
        val currentDate = Calendar.getInstance().time
        val filteredSessions = when (filter) {
            "History" -> _allTrainingSessions.value?.filter { it.first.dateTime.toDate().before(currentDate) }
            "Future" -> _allTrainingSessions.value?.filter { it.first.dateTime.toDate().after(currentDate) }
            else -> _allTrainingSessions.value
        }
        filteredTrainingSessions.value = filteredSessions ?: listOf()
    }
}


