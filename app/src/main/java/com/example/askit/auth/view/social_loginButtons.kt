package com.example.askit.auth.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.askit.R

@Composable
fun SocialLoginButtons(
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SocialButton(
            text = "Continue with Google",
            logoResId = R.drawable.google_icon,
            backgroundColor = Color.LightGray,
            textColor = Color.Black,
            onClick = onGoogleClick
        )
        Spacer(modifier = Modifier.height(12.dp))

        SocialButton(
            text = "Continue with Apple",
            logoResId = R.drawable.apple_icon,
            backgroundColor = Color.LightGray,
            textColor = Color.Black,
            onClick = onAppleClick
        )
        Spacer(modifier = Modifier.height(12.dp))

        SocialButton(
            text = "Continue with Facebook",
            logoResId = R.drawable.facebook_icon,
            backgroundColor = Color(0xFF1877F2),
            textColor = Color.White,
            onClick = onFacebookClick
        )
    }
}

@Composable
fun SocialButton(
    text: String,
    logoResId: Int,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(backgroundColor, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}