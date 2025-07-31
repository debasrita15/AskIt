package com.example.askit.data.model

data class Question(
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val authorName: String = "",
    val likes: Int = 0,
    val answersCount: Int = 0
)
