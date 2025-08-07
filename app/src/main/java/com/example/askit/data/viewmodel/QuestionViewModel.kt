package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Question
import com.example.askit.data.repository.QuestionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class QuestionViewModel(
    private val repository: QuestionRepository = QuestionRepository()
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            try {
                val result = repository.getAllQuestions()
                _questions.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postQuestion(
        title: String,
        description: String,
        category: String,
        authorName: String,
        onResult: (Boolean) -> Unit
    ) {
        val id = UUID.randomUUID().toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val question = Question(
            id = id,
            uid = uid,
            title = title,
            description = description,
            category = category,
            authorName = authorName
        )

        viewModelScope.launch {
            try {
                repository.postQuestion(question)
                fetchQuestions()
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun upvoteQuestion(questionId: String, userId: String) {
        viewModelScope.launch {
            try {
                repository.upvoteQuestion(questionId, userId)
                fetchQuestions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hasUpvoted(question: Question, userId: String): Boolean {
        return question.upvotes?.contains(userId) ?: false
    }

    fun deleteQuestion(questionId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteQuestion(questionId)
                fetchQuestions()
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun editQuestion(updatedQuestion: Question, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.editQuestion(updatedQuestion)
                fetchQuestions()
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}