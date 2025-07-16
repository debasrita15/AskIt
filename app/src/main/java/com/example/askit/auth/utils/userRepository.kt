package com.example.askit.auth.utils

import com.example.askit.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    val userProfile = UserProfile(
                        name = name,
                        email = email,
                        bio = "" // You can add a bio input if needed
                    )
                    firestore.collection("users")
                        .document(uid)
                        .set(userProfile)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener {
                            onResult(false, "Failed to save profile: ${it.message}")
                        }
                } else {
                    onResult(false, "User ID not found")
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
}
