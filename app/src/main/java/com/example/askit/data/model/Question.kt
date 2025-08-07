package com.example.askit.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Question(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: Timestamp = Timestamp.now(), //  Make nullable

    val authorName: String = "",
    @get:PropertyName("upvotes")
    @set:PropertyName("upvotes")
    var upvotes: List<String>? = emptyList(), // Make nullable for Firebase

    val edited: Boolean = false,
    val downvotes: Int = 0,
    @get:PropertyName("votedBy")
    @set:PropertyName("votedBy")
    var votedBy: Map<String, Int>? = emptyMap(), // Make nullable

    val answersCount: Int = 0,
)