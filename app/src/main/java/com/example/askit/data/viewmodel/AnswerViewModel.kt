package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Answer
import com.example.askit.data.repository.AnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnswerViewModel(
    private val repository: AnswerRepository = AnswerRepository()
) : ViewModel() {

    private val _answersMap = MutableStateFlow<Map<String, List<Answer>>>(emptyMap())
    val answersMap: StateFlow<Map<String, List<Answer>>> = _answersMap.asStateFlow()

    // Track which questions are already being collected to avoid duplicate
    private val activeCollectors = mutableSetOf<String>()


     // Loads answers for a specific question

    fun loadAnswers(questionId: String) {
        // Avoid adding multiple collectors for the same question
        if (activeCollectors.contains(questionId)) return
        activeCollectors.add(questionId)

        viewModelScope.launch {
            repository.getAnswersForQuestion(questionId)
                .distinctUntilChanged()
                .collect { answers ->
                    _answersMap.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            put(questionId, answers)
                        }
                    }
                }
        }
    }


    // Posts a new answer

    fun postAnswer(answer: Answer, onResult: (Boolean) -> Unit) {
        println("Posting answer: $answer")
        repository.postAnswer(answer) { success ->
            onResult(success)
        }
    }

    // Toggles upvote for an answer

    fun upvoteAnswer(answerId: String, userId: String, questionId: String) {
        viewModelScope.launch {
            try {
                repository.upvoteAnswer(answerId, userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Deletes an answer

    fun deleteAnswer(answerId: String, questionId: String, onResult: (Boolean) -> Unit) {
        repository.deleteAnswer(answerId) { success ->
            onResult(success)
        }
    }
// Edits answer content

    fun editAnswer(answerId: String, newContent: String, questionId: String, onResult: (Boolean) -> Unit) {
        repository.editAnswer(answerId, newContent) { success ->
            onResult(success)
        }
    }

    // Checks if the user has upvoted a particular answer.

    fun hasUpvoted(answer: Answer, userId: String): Boolean {
        return userId in answer.upvotes
    }
}
