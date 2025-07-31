package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerScreen(question: String, answer: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Answer") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)) {
            Text(text = "Q: $question", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "A: $answer", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun AnswerScreenPreview() {
    AnswerScreen("How does Rabin-Karp algorithm work?", "How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?How does Rabin-Karp algorithm work?")
}