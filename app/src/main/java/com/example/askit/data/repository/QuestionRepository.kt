package com.example.askit.data.repository

import com.example.askit.data.model.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class QuestionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val questionsRef = db.collection("questions")

    // Post a new question
    suspend fun postQuestion(question: Question) {
        if (question.id.isBlank()) throw IllegalArgumentException("Question ID cannot be blank")
        questionsRef.document(question.id).set(question).await()
    }

    //  Get all questions sorted by timestamp (used in HomeScreen)
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

    // Edit or update a question
    suspend fun editQuestion(updatedQuestion: Question) {
        questionsRef.document(updatedQuestion.id).set(updatedQuestion).await()
    }

    // Search by title or description
    suspend fun searchQuestions(query: String): List<Question> {
        val allQuestions = getAllQuestions()
        return allQuestions.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    // Get a specific question by ID
    suspend fun getQuestionById(id: String): Question? {
        return questionsRef.document(id).get().await().toObject(Question::class.java)
    }

    // Upvote question
    suspend fun upvoteQuestion(questionId: String, userId: String) {
        val docRef = questionsRef.document(questionId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val question = snapshot.toObject(Question::class.java) ?: return@runTransaction

            val upvotes = question.upvotes.toMutableList()

            if (!upvotes.contains(userId)) {
                upvotes.add(userId)
                transaction.update(docRef, "upvotes", upvotes)
            }
        }.await()
    }

    // Increment answer count
    suspend fun incrementAnswerCount(questionId: String) {
        val docRef = questionsRef.document(questionId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("answersCount") ?: 0
            transaction.update(docRef, "answersCount", currentCount + 1)
        }.await()
    }

    // Decrement answer count
    suspend fun decrementAnswerCount(questionId: String) {
        val docRef = questionsRef.document(questionId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("answersCount") ?: 1
            transaction.update(docRef, "answersCount", (currentCount - 1).coerceAtLeast(0))
        }.await()
    }

    // Fetch all questions posted by a specific user
    suspend fun getQuestionsByUser(userId: String): List<Question> {
        return questionsRef
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Question::class.java)
    }
}
