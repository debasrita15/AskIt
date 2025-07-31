package com.example.askit.data.repository

import android.util.Log
import com.example.askit.data.model.Question
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class QuestionRepository {

    private val questionsRef = Firebase.firestore.collection("questions")

    fun postQuestion(question: Question, onResult: (Boolean) -> Unit) {
        questionsRef.document(question.id).set(question)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun getAllQuestions(): Flow<List<Question>> = callbackFlow {
        val listenerRegistration = questionsRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("QuestionRepository", "Error fetching questions: ${error.message}")
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Question::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(list).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    fun getUserQuestions(uid: String): Flow<List<Question>> = callbackFlow {
        val listenerRegistration = questionsRef
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("QuestionRepository", "Error fetching user questions: ${error.message}")
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Question::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(list).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }

    fun getQuestionCount(uid: String, onResult: (Int) -> Unit) {
        questionsRef.whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.size())
            }
            .addOnFailureListener {
                onResult(0)
            }
    }

    fun deleteQuestion(questionId: String, onResult: (Boolean) -> Unit) {
        questionsRef.document(questionId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun editQuestion(updatedQuestion: Question, onResult: (Boolean) -> Unit) {
        questionsRef.document(updatedQuestion.id)
            .set(updatedQuestion)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}

