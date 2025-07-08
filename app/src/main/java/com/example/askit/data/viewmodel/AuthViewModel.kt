package com.example.askit.data.viewmodel

import com.example.askit.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = Firebase.firestore.collection("users")

    fun signUp(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                val user = User(uid, name, email)
                usersCollection.document(uid).set(user).addOnSuccessListener {
                    onResult(true)
                }.addOnFailureListener {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }

    fun logout() = auth.signOut()

    fun currentUser(): FirebaseUser? = auth.currentUser
}
