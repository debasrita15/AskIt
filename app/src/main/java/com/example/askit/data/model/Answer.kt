package com.example.askit.data.model

data class Answer(
    val id: String = "",
    val uid: String = "",
    val questionId: String = "",
    val text: String = "",
    val userName: String? = null,
    val questionTitle: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Map<String, Boolean> = emptyMap()  // key: userId, value: true
)