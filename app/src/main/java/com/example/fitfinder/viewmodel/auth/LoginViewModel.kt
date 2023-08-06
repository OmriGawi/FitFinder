package com.example.fitfinder.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.repository.auth.AuthRepository

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    val result = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        repository.login(email, password, result)
    }
}