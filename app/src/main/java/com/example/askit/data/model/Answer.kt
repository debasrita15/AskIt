package com.example.askit.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.UUID


data class Answer(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val questionId: String = "",
    val content: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val upvotes: List<String> = emptyList()
)

