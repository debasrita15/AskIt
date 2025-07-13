package com.example.askit.data.model

data class Question(
    val questionId: String = "",
    val userId: String = "",
    val title: String = "",
    val details: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val answersCount: Int = 0
)