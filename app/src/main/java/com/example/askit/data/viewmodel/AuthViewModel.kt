package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.askit.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRepository: UserRepository = UserRepository(auth)

    private val _isLoading = MutableStateFlow(false)

    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _userName = MutableStateFlow<String?>(null)


    init {
        if (auth.currentUser != null) {
            loadUserName()
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        _isLoading.value = true
        userRepository.registerUser(name, email, password) { success, message ->
            _isLoading.value = false
            _errorMessage.value = message
            if (success) {
                _userName.value = name
            }
            onResult(success, message)
        }
    }


    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        _isLoading.value = true
        userRepository.signInUser(email, password) { success, message ->
            _isLoading.value = false
            _errorMessage.value = message
            if (success) {
                loadUserName()
            }
            onResult(success, message)
        }
    }

    private fun loadUserName() {
        val uid = auth.currentUser?.uid ?: return
        userRepository.getUserProfile(uid) { profile ->
            _userName.value = profile?.name
        }
    }
}
