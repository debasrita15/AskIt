package com.example.askit.data.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.askit.R
import com.example.askit.data.model.Question
import com.example.askit.data.viewmodel.QuestionViewModel
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@Composable
fun HomeScreen(
    navController: NavController,
    questionViewModel: QuestionViewModel = viewModel()
) {
    val questions by questionViewModel.questions.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(viewModel = questionViewModel)

        // Category Dropdown
        CategoryDropdown(viewModel = questionViewModel)

        Spacer(modifier = Modifier.height(8.dp))

        // Questions List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(questions) { question ->
                QuestionCard(question = question, onClick = {
                    // Handle question click if needed
                })
            }
        }

        // FAB for asking a question
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = { navController.navigate("askQuestion") },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ask Question")
            }
        }
    }
}


@Composable
fun QuestionCard(
    question: Question,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Category: ${question.category}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Likes", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${question.likes}")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QuestionAnswer, contentDescription = "Answers", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${question.answersCount}")
                }

                val date = Date(question.timestamp)
                val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SearchBar(viewModel: QuestionViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()

    TextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search questions...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    )
}


@Composable
fun CategoryDropdown(viewModel: QuestionViewModel) {
    val categories = listOf("All", "Science", "Technology", "Health", "Education")
    var expanded by remember { mutableStateOf(false) }
    val selected by viewModel.selectedCategory.collectAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            label = { Text("Category") },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        viewModel.setSelectedCategory(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AskQuestionScreen(
    navController: NavController,
    questionViewModel: QuestionViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                    isLoading = true
                    errorMessage = null
                    questionViewModel.postQuestion(title, description, category) { success ->
                        isLoading = false
                        if (success) {
                            navController.popBackStack()
                        } else {
                            errorMessage = "Failed to post question. Try again."
                        }
                    }
                } else {
                    errorMessage = "All fields are required."
                }
            },
            modifier = Modifier.fillMaxWidth()
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