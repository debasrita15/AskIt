package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Answer
import com.example.askit.data.repository.AnswerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnswerViewModel : ViewModel() {

    private val repository = AnswerRepository()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    private val _answers = MutableStateFlow<List<Answer>>(emptyList())
    val answers: StateFlow<List<Answer>> = _answers

    private val _statusMessage = MutableStateFlow<String?>(null)

    fun fetchAnswersForQuestion(questionId: String) {
        viewModelScope.launch {
            repository.getAnswersForQuestion(questionId).collectLatest { answerList ->
                _answers.value = answerList
            }
        }
    }

    fun addAnswer(questionId: String, answerText: String) {
        val userId = uid
        if (userId.isNullOrEmpty()) {
            _statusMessage.value = "User not logged in"
            return
        }

        val answer = Answer(
            uid = userId,
            questionId = questionId,
            text = answerText,
            timestamp = System.currentTimeMillis()
        )

        repository.postAnswer(answer) { success ->
            _statusMessage.value = if (success) {
                "Answer posted successfully"
            } else {
                "Failed to post answer"
            }
        }
    }

    fun toggleLike(answerId: String, liked: Boolean,userId: String) {

        val docRef = db.collection("answers").document(answerId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            val currentLikes = try {
                @Suppress("UNCHECKED_CAST")
                snapshot.get("likes") as? Map<String, Boolean> ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }

            val updatedLikes = if (liked) {
                currentLikes + (userId to true)
            } else {
                currentLikes - userId
            }

            transaction.update(docRef, "likes", updatedLikes)
        }.addOnFailureListener {
            _statusMessage.value = "Failed to update like: ${it.message}"
        }
    }
}


