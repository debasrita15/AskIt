package com.example.askit.auth.viewmodels

import androidx.lifecycle.ViewModel
import com.example.askit.auth.utils.UserRepository
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    // Sign Up using repository (you already had this)
    fun signUp(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        UserRepository.registerUser(name, email, password, onResult)
    }

    // ✅ ADD THIS: Sign In logic using FirebaseAuth directly
    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}