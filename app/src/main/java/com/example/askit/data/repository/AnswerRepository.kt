package com.example.askit.data.repository

import com.example.askit.data.model.Answer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnswerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val answersRef = db.collection("answers")

    //  Get answers for a specific question
    fun getAnswersForQuestion(questionId: String) = callbackFlow {
        val listener = answersRef
            .whereEqualTo("questionId", questionId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val answers = snapshot.toObjects(Answer::class.java)
                trySend(answers)
            }

        awaitClose { listener.remove() }
    }

    // Post a new answer
    fun postAnswer(answer: Answer, onResult: (Boolean) -> Unit) {
        answersRef.document(answer.id)
            .set(answer)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //  Edit an existing answer
    fun editAnswer(answerId: String, newContent: String, onResult: (Boolean) -> Unit) {
        answersRef.document(answerId)
            .update("content", newContent)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete an answer
    fun deleteAnswer(answerId: String, onResult: (Boolean) -> Unit) {
        answersRef.document(answerId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    //  Upvote / remove upvote on answer
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

            transaction.update(answerRef, "upvotes", updatedUpvotes)
        }.await()
    }
}
