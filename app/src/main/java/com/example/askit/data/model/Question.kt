package com.example.askit.data.model

import com.google.firebase.firestore.PropertyName

data class Question(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val authorName: String = "",
    @get:PropertyName("upvotes")
    @set:PropertyName("upvotes")
    var upvotes: List<String> = emptyList(),
    val edited: Boolean = false,
    val downvotes: Int = 0,
    val votedBy: Map<String, Int> = emptyMap(), // userId -> 1 (upvote), -1 (downvote)
    val answersCount: Int = 0,
)