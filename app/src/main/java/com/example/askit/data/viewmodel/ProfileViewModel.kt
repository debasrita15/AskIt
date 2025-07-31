package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.example.askit.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _userQuestions = MutableStateFlow<List<Question>>(emptyList())
    val userQuestions: StateFlow<List<Question>> = _userQuestions.asStateFlow()

    private val _userAnswers = MutableStateFlow<List<Answer>>(emptyList())
    val userAnswers: StateFlow<List<Answer>> = _userAnswers.asStateFlow()

    private val _questionCount = MutableStateFlow(0)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()

    private val _answerCount = MutableStateFlow(0)
    val answerCount: StateFlow<Int> = _answerCount.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        refreshAll()
    }

    // Exposed to UI if needed
    fun refreshAll() {
        fetchUserProfile()
        fetchUserQuestions()
        fetchUserAnswers()
    }

    private fun fetchUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                _userProfile.value = doc.toObject(UserProfile::class.java)
            }
            .addOnFailureListener {
            }
    }

    private fun fetchUserQuestions() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("questions")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                val questions = result.mapNotNull { it.toObject(Question::class.java) }
                _userQuestions.value = questions
                _questionCount.value = questions.size
            }
            .addOnFailureListener {exception ->
                _errorMessage.value = exception.message}
    }

    private fun fetchUserAnswers() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("answers")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                val answers = result.mapNotNull { it.toObject(Answer::class.java) }
                _userAnswers.value = answers
                _answerCount.value = answers.size
            }
            .addOnFailureListener {exception ->
                _errorMessage.value = exception.message}
    }

    fun updateProfileImage(base64: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("profileImageBase64", base64)
            .addOnSuccessListener {
                _userProfile.value = _userProfile.value?.copy(profileImageBase64 = base64)
            }
            .addOnFailureListener {exception ->
                _errorMessage.value = exception.message}
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = null
        _userQuestions.value = emptyList()
        _userAnswers.value = emptyList()
        _questionCount.value = 0
        _answerCount.value = 0
    }
}
