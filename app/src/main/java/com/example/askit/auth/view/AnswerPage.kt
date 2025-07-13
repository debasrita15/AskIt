package com.example.askit.auth.view

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun AnswerPage(questionId: String) {
    val db = Firebase.firestore
    val answersRef = db.collection("questions").document(questionId).collection("answers")

    var answers by remember { mutableStateOf<List<Answer>>(emptyList()) }
    var question by remember { mutableStateOf<Question?>(null) }
    var userAnswer by remember { mutableStateOf("") }

    // Load question and answers
    LaunchedEffect(questionId) {
        db.collection("questions").document(questionId).get().addOnSuccessListener {
            question = it.toObject(Question::class.java)
        }

        answersRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                answers = snapshot.documents.mapNotNull { it.toObject(Answer::class.java) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Answers", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        question?.let {
            Text(it.questionId, fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                AnswerItem(answer)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            placeholder = { Text("Write your answer...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser
                val newAnswer = Answer(
                    userId = user?.displayName ?: "Anonymous",
                    text = userAnswer,
                    profilePicUrl = user?.photoUrl.toString()
                )
                answersRef.add(newAnswer)
                userAnswer = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("SUBMIT ANSWER")
        }
    }
}

@Composable
fun AnswerItem(answer: Answer) {
    Row(verticalAlignment = Alignment.Top) {
        Image(
            painter = rememberAsyncImagePainter(answer.profilePicUrl),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(answer.userId, fontWeight = FontWeight.Bold)
            Text(answer.text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnswerPagePreview() {
 
}