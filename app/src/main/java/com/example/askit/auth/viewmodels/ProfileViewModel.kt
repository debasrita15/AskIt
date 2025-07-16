package com.example.askit.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.askit.model.Answer
import com.example.askit.model.Question
import com.example.askit.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _answers = MutableStateFlow<List<Answer>>(emptyList())
    val answers: StateFlow<List<Answer>> = _answers

    fun fetchUserData() {
        viewModelScope.launch {
            uid?.let { userId ->

                // ✅ Fetch user profile correctly
                db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val bio = document.getString("bio") ?: ""

                        _userProfile.value = UserProfile(
                            name = name,
                            email = email,
                            bio = bio,
                            uid = userId
                        )
                    }

                // ✅ Fetch questions from users/{uid}/questions
                db.collection("users")
                    .document(userId)
                    .collection("questions")
                    .get()
                    .addOnSuccessListener { result ->
                        _questions.value = result.map { it.toObject(Question::class.java) }
                    }

                // ✅ Fetch answers from users/{uid}/answers
                db.collection("users")
                    .document(userId)
                    .collection("answers")
                    .get()
                    .addOnSuccessListener { result ->
                        _answers.value = result.map { it.toObject(Answer::class.java) }
                    }
            }
        }
    }
}
