package com.example.fitfinder.data.repository.sportcategories

import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SportCategoriesRepository : BaseRepository(){

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchSportCategories(): Task<QuerySnapshot> {
        return firestore.collection("sport_categories").get()
    }
}

