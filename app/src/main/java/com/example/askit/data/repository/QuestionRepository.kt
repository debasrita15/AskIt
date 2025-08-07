package com.example.askit.data.repository

import com.example.askit.data.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class QuestionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),

) {
    private val questionsRef = db.collection("questions")

    // Post a new question
    suspend fun postQuestion(question: Question) {
        if (question.id.isBlank()) throw IllegalArgumentException("Question ID cannot be blank")
        questionsRef.document(question.id).set(question).await()
    }

    //  Get all questions sorted by timestamp
    suspend fun getAllQuestions(): List<Question> {
        return questionsRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Question::class.java)
    }


    // Delete a question
    suspend fun deleteQuestion(questionId: String) {
        questionsRef.document(questionId).delete().await()
    }

    // Edit a question
    suspend fun editQuestion(updatedQuestion: Question) {
        questionsRef.document(updatedQuestion.id).set(updatedQuestion).await()
    }


    // Upvote question
    suspend fun upvoteQuestion(questionId: String, userId: String) {
        val docRef = questionsRef.document(questionId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val question = snapshot.toObject(Question::class.java) ?: return@runTransaction

            val upvotes = question.upvotes?.toMutableList() ?: mutableListOf()

            if (!upvotes.contains(userId)) {
                upvotes.add(userId)
                transaction.update(docRef, "upvotes", upvotes)
            }
        }.await()
    }
}
