package com.example.askit.data.model

data class Answer(
    val id: String = "",
    val questionId: String = "",
    val userId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0
)
