package com.example.askit.data.repository

import com.example.askit.data.model.Answer
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnswerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val answersRef = db.collection("answers")

    // Realtime fetch answers for a specific question using snapshot listener
    fun getAnswersForQuestion(questionId: String) = callbackFlow {
        val listener = answersRef
            .whereEqualTo("questionId", questionId)
            .orderBy("timestamp", Query.Direction.DESCENDING) // ⚠ Requires index
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val answers = snapshot.toObjects(Answer::class.java)
                println("Fetched answers: $answers")
                trySend(answers)
            }

        awaitClose { listener.remove() }
    }

    // Post a new answer with server-generated timestamp
    fun postAnswer(answer: Answer, onResult: (Boolean) -> Unit) {
        val answerWithTimestamp = answer.copy(timestamp = Timestamp.now())

        answersRef.document(answerWithTimestamp.id)
            .set(answerWithTimestamp)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //  Edit answer
    fun editAnswer(answerId: String, newContent: String, onResult: (Boolean) -> Unit) {
        answersRef.document(answerId)
            .update("content", newContent)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //  Delete answer
    fun deleteAnswer(answerId: String, onResult: (Boolean) -> Unit) {
        answersRef.document(answerId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Upvote or remove upvote
    suspend fun upvoteAnswer(answerId: String, userId: String) {
        val answerRef = answersRef.document(answerId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(answerRef)
            val currentUpvotes = snapshot.get("upvotes") as? List<String> ?: emptyList()

            val updatedUpvotes = if (currentUpvotes.contains(userId)) {
                currentUpvotes - userId
            } else {
                currentUpvotes + userId
            }

            transaction.update(answerRef, mapOf(
                "upvotes" to updatedUpvotes,
                "timestamp" to FieldValue.serverTimestamp() // triggers snapshot update
            ))
        }.await()
    }
}

