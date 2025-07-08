package com.example.askit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.Optional

@Composable
public fun SplashScreen(
    navController: NavController
) {
//     Optional: Scale or fade-in animation
//    val scale = remember { Animatable(0f) }

//    LaunchedEffect(Unit) {
//        scale.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(durationMillis = 800, easing = EaseOutBack)
//        )
//        delay(2000)
//        navController.navigate("signin") {
//            popUpTo("splash") { inclusive = true }
//        }
//    }
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("signin") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "AskIt",
            color = Color(0xFF005FFF),
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
//            modifier = Modifier.scale(scale.value),
            fontFamily = FontFamily.SansSerif, // Replace with custom if needed
            modifier = Modifier.align((Alignment.Center))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun preview() {
//    SplashScreen()
}
