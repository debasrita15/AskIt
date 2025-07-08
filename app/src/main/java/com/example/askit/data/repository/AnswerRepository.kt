package com.example.askit.data.repository

import com.example.askit.data.model.Answer
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AnswerRepository {
    private val answersRef = Firebase.firestore.collection("answers")

    fun postAnswer(answer: Answer, onResult: (Boolean) -> Unit) {
        val docId = answersRef.document().id
        val answerWithId = answer.copy(id = docId)
        answersRef.document(docId).set(answerWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAnswersForQuestion(questionId: String): Flow<List<Answer>> = callbackFlow {
        val listener = answersRef.whereEqualTo("questionId", questionId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(Answer::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}
