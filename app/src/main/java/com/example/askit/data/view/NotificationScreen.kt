package com.example.askit.data.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class NotificationItem(
    val title: String,
    val message: String,
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = listOf(
        NotificationItem("New Answer", "Someone answered your question."),
        NotificationItem("Badge Earned", "You earned the 'Curious' badge!"),
        NotificationItem("New Question", "Your question was posted successfully."),
        NotificationItem("Upvote Received", "Your answer got an upvote!")
    )

    // 3-color vertical gradient (blue → purple → white)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFB3E5FC), // Light Blue
            Color(0xFFD1C4E9), // Light Purple
            Color.White
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,  // Transparent background
                    titleContentColor = Color.Black,     // Or any suitable color
                    navigationIconContentColor = Color.Black
                )
            )
        }
    )
    { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(16.dp)
        ) {
            items(notifications) { notification ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3E3E3E)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = notification.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5C5C5C)
                        )
                    }
                }
            }
        }
    }
}
