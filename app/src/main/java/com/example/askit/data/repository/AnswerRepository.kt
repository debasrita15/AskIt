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
        val listenerRegistration = answersRef
            .whereEqualTo("questionId", questionId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Answer::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(list)
            }

        awaitClose { listenerRegistration.remove() }
    }
}

