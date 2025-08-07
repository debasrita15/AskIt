package com.example.askit.data.view

import android.widget.Toast
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
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.askit.data.model.Question
import com.example.askit.data.viewmodel.AnswerViewModel
import com.example.askit.data.viewmodel.QuestionViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

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

    val userName by produceState(initialValue = "User", currentUid) {
        if (currentUid.isNotBlank()) {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUid)
                .get()
                .await()
            value = snapshot.getString("name") ?: "User"
        }
    }

    LaunchedEffect(Unit) {
        questionViewModel.fetchQuestions()
    }

    LaunchedEffect(allQuestions) {
        allQuestions.forEach { question ->
            answerViewModel.loadAnswers(question.id)
        }
    }

    val filteredQuestions = allQuestions.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }

    val answerInputs = remember { mutableStateMapOf<String, String>() }

    // Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1BEE7), // Lavender
                        Color(0xFFFFF8E1), // Ivory
                        Color(0xFFFFAB91)  // Peach
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Hi, $userName",
                            color = Color(0xFFA141DC), // Ivory-Golden Accent
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.1f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate("notifications") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF4E342E))
                        }
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color(0xFF4E342E))
                        }
                    }
                )
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("askQuestion") },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color(0xFFE1BEE7),
                        contentColor = Color(0xFF4E342E),
                        text = { Text("Ask a Question",fontSize = 18.sp,
                            fontWeight = FontWeight.Bold) },
                        icon = {
                            Icon(Icons.Default.Add, contentDescription = "Ask Question")
                        }
                    )
                }
            }
        ){ paddingValues ->

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
                        Text("No questions found.", color = Color.Black)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp) // Extra padding for FAB
                    ) {
                        items(filteredQuestions) { question ->
                            QuestionCard(
                                question = question,
                                onAnswerClick = {
                                    navController.navigate("answerPage/${question.title}/${question.id}")
                                },
                                onUpvoteClick = {
                                    questionViewModel.upvoteQuestion(question.id, currentUid)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun QuestionCard(
    question: Question,
    onAnswerClick: () -> Unit,
    onUpvoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1) // Soft pale peach for contrast
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Username Initial Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val initial = question.authorName.firstOrNull()?.uppercase() ?: "U"

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF7043)), // Vibrant peach
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = question.authorName,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4E342E) // Warm deep brown
                    )
                    Text(
                        text = formatTimestamp(question.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8D6E63) // Soft cocoa
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Text
            Text(
                text = question.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF4E342E),
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upvotes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUpvoteClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${question.upvotes?.size ?: 0}",
                        color = Color(0xFF5D4037) // Cocoa
                    )
                }

                // Answers
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onAnswerClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = "Answers",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Answers",
                        color = Color(0xFF5D4037)
                    )
                }
            }
        }
    }
}



fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(timestamp.toDate())
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
        placeholder = { Text("Search questions...", color = Color.Gray) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color(0xFFFF4081), // Hot pink cursor
            focusedBorderColor = Color(0xFFFF4081),
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
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

    // Fetch user name
    LaunchedEffect(currentUid) {
        if (currentUid.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("users").document(currentUid).get()
                .addOnSuccessListener { document ->
                    authorName = document.getString("name") ?: "Anonymous"
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFCC80), // Peach
                        Color(0xFFFFF8E1)  // Ivory
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Ask a Question",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp),
                color = Color(0xFFFF7043)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF4E342E),
                    unfocusedTextColor = Color(0xFF4E342E),
                    focusedBorderColor = Color(0xFFFFA726), // Peach
                    unfocusedBorderColor = Color(0xFFBCAAA4), // Light brown
                    cursorColor = Color(0xFFFF7043),
                    focusedLabelColor = Color(0xFF6D4C41),
                    unfocusedLabelColor = Color(0xFF8D6E63),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF4E342E),
                    unfocusedTextColor = Color(0xFF4E342E),
                    focusedBorderColor = Color(0xFFFFA726), // Peach
                    unfocusedBorderColor = Color(0xFFBCAAA4), // Light brown
                    cursorColor = Color(0xFFFF7043),
                    focusedLabelColor = Color(0xFF6D4C41),
                    unfocusedLabelColor = Color(0xFF8D6E63),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )


            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF4E342E),
                    unfocusedTextColor = Color(0xFF4E342E),
                    focusedBorderColor = Color(0xFFFFA726), // Peach
                    unfocusedBorderColor = Color(0xFFBCAAA4), // Light brown
                    cursorColor = Color(0xFFFF7043),
                    focusedLabelColor = Color(0xFF6D4C41),
                    unfocusedLabelColor = Color(0xFF8D6E63),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
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
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF7043),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Post Question")
                }
            }
        }
    }
}

