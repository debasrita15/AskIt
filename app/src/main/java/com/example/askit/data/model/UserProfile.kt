package com.example.askit.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val questionsCount: Int = 0,
    val answersCount: Int = 0,
    val badges: List<String> = emptyList()
)