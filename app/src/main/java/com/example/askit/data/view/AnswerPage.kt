package com.example.askit.data.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.askit.data.model.Answer
import com.example.askit.data.viewmodel.AnswerViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerPage(
    questionTitle: String,
    questionId: String,
    navController: NavController,
    answerViewModel: AnswerViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var newAnswer by remember { mutableStateOf("") }

    LaunchedEffect(questionId) {
        answerViewModel.loadAnswers(questionId)
    }

    val answersMap by answerViewModel.answersMap.collectAsState()
    val answerList = answersMap[questionId] ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newAnswer,
                    onValueChange = { newAnswer = it },
                    placeholder = { Text("Write your answer...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                        val uid = currentUserId
                        val name = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"

                        if (newAnswer.trim().isNotEmpty()) {
                            val answer = Answer(
                                questionId = questionId,
                                content = newAnswer.trim(),
                                authorUid = uid,
                                authorName = name,
                                upvotes = emptyList()
                            )
                            answerViewModel.postAnswer(answer) { success ->
                                if (success) {
                                    newAnswer = ""
                                    Toast.makeText(context, "Answer posted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to post", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Post")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Q: $questionTitle",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (answerList.isEmpty()) {
                Text("No answers yet. Be the first to answer!")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(answerList) { answer ->
                        AnswerCard(
                            answer = answer,
                            currentUserId = currentUserId,
                            questionId = answer.questionId, // or however you're referencing it
                            viewModel = answerViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerCard(
    answer: Answer,
    currentUserId: String,
    questionId: String,
    viewModel: AnswerViewModel
)
 {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "By ${answer.authorName.ifEmpty { "Anonymous" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                viewModel.upvoteAnswer(answer.id, currentUserId, questionId)

                            },
                        tint = if (answer.upvotes.contains(currentUserId)) Color.Blue else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${answer.upvotes.size}")
                }
            }
        }
    }
}
