package com.example.askit.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Question
import com.example.askit.data.repository.QuestionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class QuestionViewModel : ViewModel() {

    private val questionRepository = QuestionRepository()
    private val auth = FirebaseAuth.getInstance()

    // Internal MutableStateFlows
    private val _allQuestions = MutableStateFlow<List<Question>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _filteredQuestions = MutableStateFlow<List<Question>>(emptyList())
    private val _myQuestions = MutableStateFlow<List<Question>>(emptyList())
    private val _questionCount = MutableStateFlow(0)

    // Exposed StateFlows
    val questions: StateFlow<List<Question>> = _filteredQuestions
    val searchQuery: StateFlow<String> = _searchQuery
    val selectedCategory: StateFlow<String> = _selectedCategory
    val myQuestions: StateFlow<List<Question>> = _myQuestions
    val questionCount: StateFlow<Int> = _questionCount

    init {
        fetchAllQuestions()
        observeFiltering()
    }

    /** Fetch all questions from Firestore */
    fun fetchAllQuestions() {
        viewModelScope.launch {
            questionRepository.getAllQuestions().collect { list ->
                _allQuestions.value = list
            }
        }
    }

    /** Post a new question to Firestore */
    fun postQuestion(title: String, description: String, category: String, onResult: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return onResult(false)
        val question = Question(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            category = category,
            timestamp = System.currentTimeMillis(),
            uid = currentUser.uid,
            authorName = currentUser.displayName ?: "Anonymous"
        )

        questionRepository.postQuestion(question) { success ->
            if (success) {
                Log.d("QuestionViewModel", "Question posted successfully.")
                fetchAllQuestions() // Optionally refresh
                fetchUserQuestions(currentUser.uid)
            } else {
                Log.e("QuestionViewModel", "Failed to post question.")
            }
        }
    }

    /** Set the search query used for filtering */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /** Set the selected category for filtering */
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    /** Apply filtering based on search and category */
    private fun observeFiltering() {
        viewModelScope.launch {
            combine(_allQuestions, _searchQuery, _selectedCategory) { questions, search, category ->
                questions.filter { question ->
                    val matchesSearch = question.title.contains(search, ignoreCase = true) ||
                            question.description.contains(search, ignoreCase = true)
                    val matchesCategory = category == "All" || question.category.equals(category, ignoreCase = true)
                    matchesSearch && matchesCategory
                }
            }.collect {
                _filteredQuestions.value = it
            }
        }
    }

    /** Fetch the current user's questions */
    fun fetchUserQuestions(uid: String) {
        viewModelScope.launch {
            questionRepository.getUserQuestions(uid).collect { questions ->
                _myQuestions.value = questions
            }
        }
    }


    /** Delete a question */
    fun deleteQuestion(questionId: String, onResult: (Boolean) -> Unit) {
        questionRepository.deleteQuestion(questionId, onResult)
    }

    /** Edit/update an existing question */
    fun editQuestion(updatedQuestion: Question, onResult: (Boolean) -> Unit) {
        questionRepository.editQuestion(updatedQuestion, onResult)
    }
}

