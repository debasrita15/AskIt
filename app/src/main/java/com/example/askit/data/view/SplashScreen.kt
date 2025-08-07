package com.example.askit.data.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        delay(1500)

        if (user != null) {
            try {
                val doc = db.collection("users").document(user.uid).get().await()
                if (doc.exists()) {
                    onNavigateToHome()
                } else {
                    onNavigateToSignIn()
                }
            } catch (e: Exception) {
                onNavigateToSignIn()
            }
        } else {
            onNavigateToSignIn()
        }

        isLoading = false
    }

    // 🟦 Gradient Background (Soft + Stylish)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF005FFF),   // Electric Blue
                        Color(0xFF7F5AF0),   // Vivid Indigo
                        Color(0xFFF1F6FB)    // Creamy White
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = showContent, enter = fadeIn()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // 💡 Glowing Lightbulb Icon
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "AskIt Lightbulb",
                    tint = Color(0xFFF1F6FB), // Light yellow
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(12.dp, shape = CircleShape, clip = false)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 🔤 App Name with bounce-in scale animation
                val scale = remember { Animatable(0.8f) }
                LaunchedEffect(true) {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 800, easing = EaseOutBack)
                    )
                }

                Text(
                    text = "AskIt",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.scale(scale.value)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ✨ Tagline
                Text(
                    text = "Where curiosity begins.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFDAE5FF),
                    textAlign = TextAlign.Center
                )

                if (isLoading) {
                    Spacer(modifier = Modifier.height(30.dp))
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
