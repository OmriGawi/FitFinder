package com.example.fitfinder.viewmodel.sportcategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.google.firebase.firestore.FirebaseFirestore

class SportCategoriesViewModel(private val repository: SportCategoriesRepository) : ViewModel() {

    private val _sportCategories = MutableLiveData<List<String>>()
    val sportCategories: LiveData<List<String>> get() = _sportCategories

    private val _exercises = MutableLiveData<List<String>>()
    val exercises: LiveData<List<String>> get() = _exercises

    fun fetchSportCategories() {
        repository.fetchSportCategories().addOnSuccessListener { querySnapshot ->
            val categoriesList = querySnapshot.documents.map { it.getString("name") ?: "" }
            _sportCategories.value = categoriesList
        }
    }

    fun fetchExercisesForCategory(categoryName: String) {
        repository.fetchExercisesForCategory(categoryName).addOnSuccessListener { querySnapshot ->
            val exercisesList = querySnapshot.documents.flatMap { document ->
                (document.get("exercises") as? List<String>) ?: emptyList()
            }
            _exercises.value = exercisesList
        }
    }
}


