package com.example.askit.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class Answer(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),

    @get:PropertyName("questionId")
    @set:PropertyName("questionId")
    var questionId: String = "",

    @get:PropertyName("content")
    @set:PropertyName("content")
    var content: String = "",

    @get:PropertyName("authorUid")
    @set:PropertyName("authorUid")
    var authorUid: String = "",

    @get:PropertyName("authorName")
    @set:PropertyName("authorName")
    var authorName: String = "",

    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: Long = System.currentTimeMillis(),

    @get:PropertyName("upvotes")
    @set:PropertyName("upvotes")
    var upvotes: List<String> = emptyList(),

    @get:PropertyName("votedBy")
    @set:PropertyName("votedBy")
    var votedBy: Map<String, Int> = emptyMap()
)
