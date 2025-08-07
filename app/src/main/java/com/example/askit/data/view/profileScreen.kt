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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    // Cyan to Blue Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF1F6FB),
                        Color(0xFF7F5AF0),
                        Color(0xFF005FFF)
                    )
                )
            )
    ) {
        userProfile?.let { profile ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Gradient Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF8E24AA), Color(0xFF3949AB))
                            )
                        )
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

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = profile.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(label = "Questions", count = questionCount, modifier = Modifier.weight(1f))
                    StatCard(label = "Answers", count = answerCount, modifier = Modifier.weight(1f))
                    StatCard(label = "Badges", count = badges.size, modifier = Modifier.weight(1f))
                }


                Spacer(modifier = Modifier.height(30.dp))

                if (badges.isNotEmpty()) {
                    Text(
                        text = "Earned Badges",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(badges) { badge ->
                            BadgeCard(badge = badge) { selectedBadge = badge }
                        }
                    }

                    Spacer(modifier = Modifier.height(34.dp))
                }

                // Menu Options
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        val menuGradient = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF42A5F5), // Light Blue
                                Color(0xFF1E88E5), // Medium Blue
                                Color(0xFF3949AB)  // Indigo Blue
                            )
                        )

                        MenuCard(
                            text = "My Questions",
                            onClick = onMyQuestionsClick,
                            gradient = menuGradient,
                            icon = Icons.Default.QuestionAnswer
                        )
                        MenuCard(
                            text = "My Answers",
                            onClick = onMyAnswersClick,
                            gradient = menuGradient,
                            icon = Icons.Default.ThumbUp
                        )
                        MenuCard(
                            text = "Settings",
                            onClick = onSettingsClick,
                            gradient = menuGradient,
                            icon = Icons.Default.Settings
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Logout Button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935), // Bold red
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }

    selectedBadge?.let {
        BadgeDetailDialog(badge = it, onDismiss = { selectedBadge = null })
    }

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
fun MenuCard(
    text: String,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    gradient: Brush,
    icon: ImageVector? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradient)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 10.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}




@Composable
fun StatCard(label: String, count: Int, modifier: Modifier = Modifier) {
    val statGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF42A5F5), // Light Blue
            Color(0xFF1E88E5), // Medium Blue
            Color(0xFF3949AB)  // Indigo Blue
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(statGradient)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}



@Composable
fun BadgeCard(
    badge: Badge,
    onClick: (Badge) -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 70.dp)
            .clickable { onClick(badge) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent // Use custom gradient background
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3), // Bright Blue
                            Color(0xFF9C27B0)  // Deep Purple
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents, // Trophy icon
                    contentDescription = "Badge Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = badge.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.width(280.dp)
            ) {
                // Trophy icon
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFC107), // Trophy gold
                    modifier = Modifier.size(60.dp)
                )

                Text(
                    text = "You've unlocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = badge.description,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4081)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Awesome!", color = Color.White)
                }
            }
        }
    }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE0B2), // Light Peach
                        Color(0xFFFFF3E0)  // Ivory
                    )
                )
            )
            .padding(12.dp)
    ) {
        if (myQuestions.isEmpty()) {
            // Show message if no questions
            Text(
                text = "You haven't asked any questions yet.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn {
                items(myQuestions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("answers/${question.id}")
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1) // Very light peach
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {

                            Text(
                                text = question.authorName + if (question.edited) " (edited)" else "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = question.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = question.description ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Upvote",
                                        tint = if (questionViewModel.hasUpvoted(question, myUid)) Color(0xFFFF5722) else Color.Gray,
                                        modifier = Modifier.clickable {
                                            questionViewModel.upvoteQuestion(question.id, myUid)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${question.upvotes?.size ?: 0}")

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Icon(
                                        imageVector = Icons.Default.Comment,
                                        contentDescription = "Answers",
                                        tint = Color.Gray,
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

    val customColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF4E342E),
        unfocusedTextColor = Color(0xFF4E342E),
        focusedBorderColor = Color(0xFFFFA726),       // Peach
        unfocusedBorderColor = Color(0xFFBCAAA4),     // Light brown
        cursorColor = Color(0xFFFF7043),              // Coral
        focusedLabelColor = Color(0xFF6D4C41),
        unfocusedLabelColor = Color(0xFF8D6E63),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    colors = customColors,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = customColors,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    colors = customColors,
                    singleLine = true
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
                Text("Save", color = Color(0xFFFF6F00)) // Orange accent
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

    val allAnswers = answersMap.values.flatten()
    val userAnswers = allAnswers.filter { it.authorUid == currentUserId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Answers",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,  // Transparent background
                    titleContentColor = Color.Black,     // Or any suitable color
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        if (userAnswers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFE0B2), // Light Peach
                                Color(0xFFFFF3E0)  // Ivory
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "You haven't posted any answers yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userAnswers) { answer ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1) // Very light peach
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Answered on:",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = answer.content.ifEmpty { answer.questionId },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color(0xFF5D4037) // Deep brown
                                    )
                                }

                                Row {
                                    IconButton(onClick = { editingAnswer = answer }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = Color(0xFFFFA726) // Peach
                                        )
                                    }
                                    IconButton(onClick = {
                                        answerViewModel.deleteAnswer(answer.id, answer.questionId) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Answer deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFD32F2F) // Red
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = answer.content,
                                fontSize = 14.sp,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 20.sp,
                                color = Color(0xFF4E342E)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Upvote",
                                tint = if (answerViewModel.hasUpvoted(answer, userId = String())) Color(0xFFFF5722) else Color.Gray,
                                modifier = Modifier.clickable {
                                    answerViewModel.upvoteAnswer(answer.id, userId = String(), answer.questionId)
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${answer.upvotes?.size ?: 0}")
                        }
                        }
                    }
                }
            }

            // Dialog for editing answer
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




@Composable
fun EditAnswerDialog(
    original: Answer,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var content by remember { mutableStateOf(original.content) }

    val customColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF4E342E),
        unfocusedTextColor = Color(0xFF4E342E),
        focusedBorderColor = Color(0xFFFFA726),       // Peach
        unfocusedBorderColor = Color(0xFFBCAAA4),     // Light brown
        cursorColor = Color(0xFFFF7043),              // Coral
        focusedLabelColor = Color(0xFF6D4C41),
        unfocusedLabelColor = Color(0xFF8D6E63),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Answer", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Your Answer") },
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                colors = customColors
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(content) }) {
                Text("Save", color = Color(0xFFFF6F00)) // Orange accent
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
