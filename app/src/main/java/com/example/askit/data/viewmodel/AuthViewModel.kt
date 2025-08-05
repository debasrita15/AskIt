package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.askit.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun signUp(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        userRepository.registerUser(name, email, password) { success, message ->
            _isLoading.value = false
            _errorMessage.value = message
            onResult(success, message)
        }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        _isLoading.value = true
        userRepository.signInUser(email, password) { success, message ->
            _isLoading.value = false
            _errorMessage.value = message
            onResult(success, message)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
