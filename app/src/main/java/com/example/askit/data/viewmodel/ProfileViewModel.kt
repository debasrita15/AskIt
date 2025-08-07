package com.example.askit.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.example.askit.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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


    init {
        refreshAll()
    }

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
                _errorMessage.value = it.message
            }
    }

    private fun fetchUserQuestions() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("questions")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _errorMessage.value = error.message
                    println(" Error fetching questions: ${error.message}")
                    return@addSnapshotListener
                }

                println("Snapshot docs: ${snapshot?.documents?.size}")

                val questions = snapshot?.documents
                    ?.mapNotNull { it.toObject(Question::class.java) }
                    ?.sortedByDescending { it.timestamp } ?: emptyList()

                println("Mapped questions: $questions")

                _userQuestions.value = questions
                _questionCount.value = questions.size
            }
    }


    private fun fetchUserAnswers() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("answers")
            .whereEqualTo("authorUid", uid)
            .get()
            .addOnSuccessListener { result ->
                val answers = result.mapNotNull { it.toObject(Answer::class.java) }
                _userAnswers.value = answers
                _answerCount.value = answers.size
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = exception.message
            }
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