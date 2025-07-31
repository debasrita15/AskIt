package com.example.askit.data.repository

import android.util.Log
import com.example.askit.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()

        auth.createUserWithEmailAndPassword(normalizedEmail, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    val userProfile = UserProfile(
                        uid = uid,
                        name = name,
                        email = normalizedEmail
                    )
                    firestore.collection("users")
                        .document(uid)
                        .set(userProfile)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            Log.e("UserRepository", "Profile save error: ${it.message}")
                            onResult(false, "Failed to save profile: ${it.message}")
                        }
                } else {
                    onResult(false, "User ID not found")
                }
            }
            .addOnFailureListener {
                Log.e("UserRepository", "Register error: ${it.message}")
                onResult(false, it.message)
            }
    }

    fun signInUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()

        auth.signInWithEmailAndPassword(normalizedEmail, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                Log.e("UserRepository", "Sign-in error: ${it.message}")
                onResult(false, it.message)
            }
    }

    fun getUserProfile(
        uid: String,
        onResult: (UserProfile?) -> Unit
    ) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                onResult(profile)
            }
            .addOnFailureListener {
                Log.e("UserRepository", "Get profile error: ${it.message}")
                onResult(null)
            }
    }
}
