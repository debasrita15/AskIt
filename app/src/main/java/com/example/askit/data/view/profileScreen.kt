package com.example.askit.data.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.askit.data.viewmodel.ProfileViewModel
import com.example.askit.data.badges.BadgeMilestones
import com.example.askit.data.model.Answer
import com.example.askit.data.model.Question
import com.example.askit.data.viewmodel.AnswerViewModel
import com.example.askit.data.viewmodel.QuestionViewModel
import com.google.firebase.auth.FirebaseAuth


data class Badge(
    val title: String,
    val description: String
)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyQuestionsClick: () -> Unit,
    onMyAnswersClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val userQuestions by viewModel.userQuestions.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val questionCount by viewModel.questionCount.collectAsState()
    val answerCount by viewModel.answerCount.collectAsState()

    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val badges by remember(userQuestions, userAnswers) {
        derivedStateOf {
            val earned = mutableListOf<Badge>()
            BadgeMilestones.questionMilestones.forEach { (count, title) ->
                if (userQuestions.size >= count) {
                    earned.add(Badge(title, "Asked $count questions"))
                }
            }
            BadgeMilestones.answerMilestones.forEach { (count, title) ->
                if (userAnswers.size >= count) {
                    earned.add(Badge(title, "Answered $count questions"))
                }
            }
            earned
        }
    }

    userProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Initial Avatar Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4C6EF5), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Username
            Text(
                text = profile.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFC)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(label = "Questions", count = questionCount)
                    StatCard(label = "Answers", count = answerCount)
                    StatCard(label = "Badges", count = badges.size)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Badges Section
            if (badges.isNotEmpty()) {
                Text(
                    text = "Earned Badges",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(badges) { badge ->
                        BadgeCard(badge = badge) { selectedBadge = badge }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Menu Options
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5F7))
            ) {
                Column {
                    MenuCard("My Questions", onClick = onMyQuestionsClick)
                    Divider()
                    MenuCard("My Answers", onClick = onMyAnswersClick)
                    Divider()
                    MenuCard("Settings", onClick = onSettingsClick)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    // Badge Detail Dialog
    selectedBadge?.let {
        BadgeDetailDialog(badge = it, onDismiss = { selectedBadge = null })
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                    onLogout()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Composable
fun MenuCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
    }
}


@Composable
fun StatCard(label: String, count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = label, fontSize = 14.sp)
        }
    }
}


@Composable
fun BadgeCard(badge: Badge, onClick: (Badge) -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 60.dp)
            .clickable { onClick(badge) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF4A90E2))
            Text(badge.title, fontWeight = FontWeight.Medium, fontSize = 12.sp)
        }
    }
}

@Composable
fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = { Text(badge.title, fontWeight = FontWeight.Bold) },
        text = { Text(badge.description) }
    )
}


@Composable
fun MyQuestionsScreen(
    questionViewModel: QuestionViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val myUid = currentUser?.uid ?: ""
    val allQuestions by questionViewModel.questions.collectAsState()
    var editingQuestion by remember { mutableStateOf<Question?>(null) }

    LaunchedEffect(Unit) {
        questionViewModel.fetchQuestions()
    }

    val myQuestions = allQuestions.filter { it.uid == myUid }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(myQuestions) { question ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("answers/${question.id}")
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = question.authorName + if (question.edited) " (edited)" else "",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = question.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = question.description ?: "", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Upvote",
                                tint = if (questionViewModel.hasUpvoted(question, myUid)) Color.Blue else Color.Gray,
                                modifier = Modifier.clickable {
                                    questionViewModel.upvoteQuestion(question.id, myUid)
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${question.upvotes.size}")

                            Spacer(modifier = Modifier.width(16.dp))

                            Icon(
                                imageVector = Icons.Default.Comment,
                                contentDescription = "Answers",
                                modifier = Modifier.clickable {
                                    navController.navigate("answers/${question.id}")
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${question.answersCount}")
                        }

                        Row {
                            IconButton(onClick = {
                                editingQuestion = question
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }

                            IconButton(onClick = {
                                questionViewModel.deleteQuestion(question.id) {
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (editingQuestion != null) {
        EditQuestionDialog(
            original = editingQuestion!!,
            onDismiss = { editingQuestion = null },
            onConfirm = { updated ->
                questionViewModel.editQuestion(updated) {
                    Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show()
                }
                editingQuestion = null
            }
        )
    }
}


@Composable
fun EditQuestionDialog(
    original: Question,
    onDismiss: () -> Unit,
    onConfirm: (Question) -> Unit
) {
    var title by remember { mutableStateOf(original.title) }
    var description by remember { mutableStateOf(original.description ?: "") }
    var category by remember { mutableStateOf(original.category ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updated = original.copy(
                    title = title,
                    description = description,
                    category = category,
                    edited = true
                )
                onConfirm(updated)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAnswersScreen(
    answerViewModel: AnswerViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val answersMap by answerViewModel.answersMap.collectAsState()
    var editingAnswer by remember { mutableStateOf<Answer?>(null) }

    // Flatten map and filter by current user
    val allAnswers = answersMap.values.flatten()
    val userAnswers = allAnswers.filter { it.authorUid == currentUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Answers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (userAnswers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven’t answered any questions yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userAnswers) { answer ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text("In response to:", fontSize = 12.sp, color = Color.Gray)
                                    Text(answer.questionId, fontWeight = FontWeight.SemiBold)
                                    // You can fetch and display actual question title here
                                }
                                Row {
                                    IconButton(onClick = { editingAnswer = answer }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        answerViewModel.deleteAnswer(answer.id, answer.questionId) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Answer deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = answer.content,
                                fontSize = 14.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upvotes: ${answer.upvotes.size}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            editingAnswer?.let { answer ->
                EditAnswerDialog(
                    original = answer,
                    onDismiss = { editingAnswer = null },
                    onConfirm = { updatedContent ->
                        answerViewModel.editAnswer(answer.id, updatedContent, answer.questionId) { success ->
                            if (success) {
                                Toast.makeText(context, "Answer updated", Toast.LENGTH_SHORT).show()
                                editingAnswer = null
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun EditAnswerDialog(
    original: Answer,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var content by remember { mutableStateOf(original.content) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Answer") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your Answer") },
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(content) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}