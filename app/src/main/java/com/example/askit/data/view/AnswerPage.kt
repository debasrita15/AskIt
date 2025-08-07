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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.askit.data.model.Answer
import com.example.askit.data.viewmodel.AnswerViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerPage(
    questionId: String,
    questionTitle: String,
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

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFE0B2), Color(0xFFFFF8E1)) // Peach to Ivory
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Answers",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E) // Dark brown for contrast
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF4E342E)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(16.dp)) // Match AnswerCard background
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newAnswer,
                        onValueChange = { newAnswer = it },
                        placeholder = { Text("Write your answer...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7043),   // Vibrant peach-orange
                            unfocusedBorderColor = Color(0xFFBCAAA4), // Warm light brown
                            cursorColor = Color(0xFFFB8C00),
                            focusedTextColor = Color(0xFF4E342E),
                            unfocusedTextColor = Color(0xFF4E342E),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
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
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7043), // Match upvote button
                            contentColor = Color.White
                        )
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
                    text = "${answerList.size} Answer${if (answerList.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFF6D4C41)
                )

                if (answerList.isEmpty()) {
                    Text(
                        "Don't leave this question hanging.Be the first one to help!",
                        color = Color(0xFF4E342E),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(answerList) { answer ->
                            AnswerCard(
                                answer = answer,
                                currentUserId = currentUserId,
                                questionId = answer.questionId,
                                viewModel = answerViewModel
                            )
                        }
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
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0) // Light cream (peachy tone)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF4E342E) // Deep warm brown
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "By ${answer.authorName.ifEmpty { "Anonymous" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8D6E63) // Muted brown-gray
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
                        tint = if (answer.upvotes.contains(currentUserId))
                            Color(0xFFFF7043) // Vibrant peach-orange when upvoted
                        else
                            Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${answer.upvotes.size}",
                        color = Color(0xFF5D4037) // Dark cocoa
                    )
                }
            }
        }
    }
}
