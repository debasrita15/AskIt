package com.example.askit.data.repository

import com.example.askit.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {
    private val usersRef = Firebase.firestore.collection("users")

    fun getUserById(uid: String): Flow<User?> = callbackFlow {
        val listener = usersRef.document(uid)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        usersRef.document(user.uid).set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
