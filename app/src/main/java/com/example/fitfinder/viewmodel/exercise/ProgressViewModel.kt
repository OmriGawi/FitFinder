package com.example.fitfinder.viewmodel.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.ExerciseProgress
import com.example.fitfinder.data.repository.exercise.ProgressRepository

class ProgressViewModel(private val repository: ProgressRepository) : ViewModel() {

    private val _progressData = MutableLiveData<List<ExerciseProgress>>()
    val progressData: LiveData<List<ExerciseProgress>> = _progressData

    fun fetchProgressData(userId: String) {
        repository.fetchUserProgress(userId) { progressList ->
            _progressData.postValue(progressList)
        }
    }
}