package com.example.askit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.example.askit.auth.view.SignInScreen
import com.example.askit.auth.view.SignUpScreen
import com.example.askit.ui.theme.AskitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AskitTheme  {
                Surface(modifier = Modifier.fillMaxSize(),
                color= Color.White
                ) {
                    AskitAuthScreen()
                }
            }
        }
    }
}

@Composable
fun AskitAuthScreen() {
    var isSignIn by remember { mutableStateOf(true) }

    if (isSignIn) {
        SignInScreen(onSwitchToSignUp = { isSignIn = false })
    } else {
        SignUpScreen(onSwitchToSignIn = { isSignIn = true })
    }
}