package com.example.askit.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profilePicUrl: String = "",
    val answers: Int = 0,
    val questions: Int = 0,
    val badges: Int = 0
)
