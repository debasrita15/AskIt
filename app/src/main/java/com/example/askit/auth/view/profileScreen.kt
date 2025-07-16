package com.example.askit.auth.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Settings
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
import com.example.askit.R
import com.example.askit.auth.viewmodels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.collectAsState
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        key = FirebaseAuth.getInstance().currentUser?.uid
    )
)

 {
    val userProfile by viewModel.userProfile.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val answers by viewModel.answers.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.profile_icon),
            contentDescription = "Profile",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(userProfile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // Optional UID (for testing)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "No UID"
        Text(text = "UID: $uid", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Edit Profile */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Edit Profile", color = Color.White)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(count = answers.size.toString(), label = "Answers")
            StatItem(count = questions.size.toString(), label = "Questions")
            StatItem(count = "4", label = "Badges")
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileOption(icon = Icons.Default.Bookmark, label = "My Questions")
        ProfileOption(icon = Icons.Default.FormatListBulleted, label = "My Answers")
        ProfileOption(icon = Icons.Default.Settings, label = "Settings")

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Logout", color = Color.White)
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 14.sp, color = Color.DarkGray)
    }
}

@Composable
fun ProfileOption(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontSize = 16.sp)
        }
    }
}
