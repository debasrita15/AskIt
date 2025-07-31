package com.example.askit.data.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.example.askit.data.viewmodel.AnswerViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnswerPage(
    questionId: String,
    onBackPressed: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val viewModel: AnswerViewModel = viewModel()
    var question by remember { mutableStateOf<Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }

    val answers by viewModel.answers.collectAsState()

    LaunchedEffect(questionId) {
        db.collection("questions").document(questionId).get().addOnSuccessListener {
            question = it.toObject(Question::class.java)?.copy(id = it.id)
        }
        viewModel.fetchAnswersForQuestion(questionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBackPressed() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Answers", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Display
        question?.let {
            Text(it.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFEAEAEA), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(it.category, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Answers", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(answers) { answer ->
                AnswerItem(answer = answer, viewModel = viewModel)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            placeholder = { Text("Write your answer...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                if (userAnswer.isNotBlank()) {
                    viewModel.addAnswer(questionId, userAnswer)
                    userAnswer = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SUBMIT ANSWER")
        }
    }
}

@Composable
fun AnswerItem(answer: Answer, viewModel: AnswerViewModel) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val liked = remember(answer.likes, uid) { uid != null && (answer.likes?.containsKey(uid) == true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F1F1), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = answer.text, fontSize = 16.sp)
        }

        IconToggleButton(
            checked = liked,
            onCheckedChange = {
                uid?.let {
                    viewModel.toggleLike(answerId = answer.id, liked = !liked, userId = it)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Like",
                tint = if (liked) Color.Red else Color.Gray
            )
        }

        Text("${answer.likes?.size ?: 0}", modifier = Modifier.padding(start = 4.dp))
    }
}
