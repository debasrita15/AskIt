package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Answer
import com.example.askit.data.repository.AnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnswerViewModel(
    private val repository: AnswerRepository = AnswerRepository()
) : ViewModel() {

    private val _answersMap = MutableStateFlow<Map<String, List<Answer>>>(emptyMap())
    val answersMap: StateFlow<Map<String, List<Answer>>> = _answersMap.asStateFlow()

    // Load answers for a specific question
    fun loadAnswers(questionId: String) {
        viewModelScope.launch {
            repository.getAnswersForQuestion(questionId).collect { answers ->
                _answersMap.value = _answersMap.value.toMutableMap().apply {
                    this[questionId] = answers
                }
            }
        }
    }

    // Post a new answer
    fun postAnswer(answer: Answer, onResult: (Boolean) -> Unit) {
        repository.postAnswer(answer) { success ->
            if (success) loadAnswers(answer.questionId)
            onResult(success)
        }
    }

    // Upvote or remove upvote
    fun upvoteAnswer(answerId: String, userId: String, questionId: String) {
        viewModelScope.launch {
            try {
                repository.upvoteAnswer(answerId, userId)
                // Optional reload: Firestore snapshot listener already updates if used correctly
                loadAnswers(questionId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Check if current user has upvoted an answer
    fun hasUpvoted(answer: Answer, userId: String): Boolean {
        return answer.upvotes.contains(userId)
    }

    // Delete answer
    fun deleteAnswer(answerId: String, questionId: String, onResult: (Boolean) -> Unit) {
        repository.deleteAnswer(answerId) { success ->
            if (success) loadAnswers(questionId)
            onResult(success)
        }
    }

    // Edit answer
    fun editAnswer(answerId: String, newContent: String, questionId: String, onResult: (Boolean) -> Unit) {
        repository.editAnswer(answerId, newContent) { success ->
            if (success) loadAnswers(questionId)
            onResult(success)
        }
    }
}
