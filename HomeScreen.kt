package com.example.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(userName: String = "Welcome, Saurdeep") {
    TopAppBar(
        title = { Text(userName) },
        actions = {
            IconButton(onClick = { /* Navigate to Profile */ }) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskQuestionDialog(showDialog: Boolean, onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    if (showDialog) {
        var questionText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Ask a Question") },
            text = {
                TextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    placeholder = { Text("Type your question here") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onSubmit(questionText)
                    onDismiss()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = { query = it },
        placeholder = { Text("Search questions...") },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
    )
}

@Composable
fun CategoryFilterBar() {
    val categories = listOf("All", "Science", "Math", "Tech", "Arts")
    LazyRow {
        items(categories) { category ->
            Button(onClick = { /* filter logic */ }, modifier = Modifier.padding(end = 8.dp)) {
                Text(category)
            }
        }
    }
}


@Composable
fun HomeScreen() {
    var showDialog by remember { mutableStateOf(false) }
    val questions = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        TopBar()
        SearchBar()
        CategoryFilterBar()

        LazyColumn {
            items(questions) { question ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
//                            navController.navigate("answer/${question}")
                        }
                ) {
                    Text(
                        text = question,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        FloatingActionButton(onClick = { showDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Ask Question")
        }

        AskQuestionDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onSubmit = { newQuestion ->
                questions.add(newQuestion)
            }
        )
    }
}