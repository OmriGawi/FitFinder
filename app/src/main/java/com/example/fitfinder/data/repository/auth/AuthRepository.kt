package com.example.fitfinder.data.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.fitfinder.data.model.User
import com.example.fitfinder.data.repository.BaseRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository : BaseRepository() {
    private val auth = FirebaseAuth.getInstance()

    fun register(firstName: String, lastName: String, birthDate: Timestamp, email: String, password: String, result: MutableLiveData<Boolean>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration succeeded
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val user = userId?.let { User(it, firstName, lastName, birthDate, email) }
                    userId?.let {
                        user?.let { it1 ->
                            FirebaseFirestore.getInstance().collection("users").document(it).set(it1)
                                .addOnCompleteListener { userCreationTask ->
                                    result.value = userCreationTask.isSuccessful
                                }
                        }
                    }
                } else {
                    // Registration failed
                    result.value = false
                }
            }
    }

    fun login(email: String, password: String, result: MutableLiveData<Boolean>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = task.isSuccessful
            }
    }

}