package com.example.askit.data.view

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.example.askit.data.viewmodel.AnswerViewModel
import com.example.askit.data.viewmodel.QuestionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    questionViewModel: QuestionViewModel = viewModel(),
    answerViewModel: AnswerViewModel = viewModel()
) {
    val context = LocalContext.current
    val answerMap by answerViewModel.answersMap.collectAsState()
    val allQuestions by questionViewModel.questions.collectAsState()
    val query by questionViewModel.searchQuery.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUid = currentUser?.uid.orEmpty()

    var userName by remember { mutableStateOf("User") }

    // Fetch user's name from Firestore
    LaunchedEffect(currentUid) {
        if (currentUid.isNotBlank()) {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUid)
                .get()
                .await()
            userName = snapshot.getString("name") ?: "User"
        }
    }

    // Fetch all questions and their answers
    LaunchedEffect(Unit) {
        questionViewModel.fetchQuestions()
    }

    // Ensure all answers are fetched in response to question updates
    LaunchedEffect(allQuestions) {
        allQuestions.forEach { question ->
            answerViewModel.loadAnswers(question.id)
        }
    }

    // Realtime search
    val filteredQuestions = allQuestions.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }

    val answerInputs = remember { mutableStateMapOf<String, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hi, $userName") },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("askQuestion")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ask Question")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchBar(
                query = query,
                onQueryChange = { questionViewModel.setSearchQuery(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredQuestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text("No questions found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredQuestions) { question ->
                        val answersForQuestion = answerMap[question.id] ?: emptyList()
                        val answerText = answerInputs[question.id] ?: ""

                        QuestionCard(
                            question = question,
                            answers = answersForQuestion,
                            onAnswerClick = {},
                            onUpvoteAnswer = { answer ->
                                answerViewModel.upvoteAnswer(answer.id, currentUid, question.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerInputs[question.id] = it },
                            label = { Text("Write your answer...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                val content = answerInputs[question.id]
                                if (!content.isNullOrBlank()) {
                                    val newAnswer = Answer(
                                        questionId = question.id,
                                        content = content,
                                        authorUid = currentUid,
                                        authorName = userName
                                    )
                                    answerViewModel.postAnswer(newAnswer) {
                                        answerInputs[question.id] = ""
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text("Post Answer")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun QuestionCard(
    question: Question,
    answers: List<Answer>,
    onAnswerClick: () -> Unit,
    onUpvoteAnswer: (Answer) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Question Info
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val authorInitial = question.authorName.firstOrNull()?.uppercase() ?: "?"
                val backgroundColor = remember(question.authorName) {
                    val colors = listOf(
                        Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF42A5F5),
                        Color(0xFF26A69A), Color(0xFFFF7043), Color(0xFFFFCA28)
                    )
                    val index = (question.authorName.hashCode().absoluteValue % colors.size)
                    colors[index]
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                ) {
                    Text(
                        text = authorInitial,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = question.authorName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = formatTimestamp(question.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            // Answer Section
            if (answers.isNotEmpty()) {
                Text(
                    text = "Answers:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                answers.forEach { answer ->
                    AnswerPreview(answer = answer, onUpvoteClick = {
                        onUpvoteAnswer(answer)
                    })
                }
            } else {
                Text(
                    text = "No answers yet. Be the first to respond!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Answer Button
            Button(
                onClick = onAnswerClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Write an Answer")
            }
        }
    }
}


@Composable
fun AnswerPreview(
    answer: Answer,
    onUpvoteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val maxPreviewLength = 150
    val shouldTruncate = answer.content.length > maxPreviewLength
    val authorInitial = answer.authorName.firstOrNull()?.uppercase() ?: "?"

    // Generate consistent color based on username hash
    val backgroundColor = remember(answer.authorName) {
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF42A5F5),
            Color(0xFF26A69A), Color(0xFFFF7043), Color(0xFFFFCA28)
        )
        val index = (answer.authorName.hashCode().absoluteValue % colors.size)
        colors[index]
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Colored circle with initial
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            ) {
                Text(
                    text = authorInitial,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = answer.authorName,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (expanded || !shouldTruncate) answer.content
            else answer.content.take(maxPreviewLength) + "...",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable { expanded = !expanded }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatTimestamp(answer.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onUpvoteClick) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Upvote"
                )
            }
            Text(
                text = "${answer.upvotes} Upvotes",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}



fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = { onQueryChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search questions...") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors()
    )
}


@Composable
fun AskQuestionScreen(
    navController: NavController,
    questionViewModel: QuestionViewModel = viewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val currentUid = currentUser?.uid ?: ""
    var authorName by remember { mutableStateOf("Anonymous") }

    // Fetch user name once
    LaunchedEffect(currentUid) {
        if (currentUid.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
                .addOnSuccessListener { document ->
                    authorName = document.getString("name") ?: "Anonymous"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ask a Question", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 10
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (title.isBlank() || description.isBlank() || category.isBlank()) {
                    errorMessage = "All fields are required."
                    return@Button
                }

                if (currentUser == null) {
                    Toast.makeText(context, "You must be logged in to post a question", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                errorMessage = null

                questionViewModel.postQuestion(
                    title = title.trim(),
                    description = description.trim(),
                    category = category.trim(),
                    authorName = authorName
                ) { success ->
                    isLoading = false
                    if (success) {
                        Toast.makeText(context, "Question posted", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        errorMessage = "Failed to post question."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Post Question")
            }
        }
    }
}
