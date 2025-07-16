package com.example.askit.data.repository

import com.example.askit.data.model.Question
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.Query


class QuestionRepository {
    private val questionsRef = Firebase.firestore.collection("questions")

    fun postQuestion(question: Question, onResult: (Boolean) -> Unit) {
        val docId = questionsRef.document().id
        val questionWithId = question.copy(questionId = docId)
        questionsRef.document(docId).set(questionWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAllQuestions(): Flow<List<Question>> = callbackFlow {
        val listener = questionsRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(Question::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}
