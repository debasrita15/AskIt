package com.example.askit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.askit.ui.screens.SplashScreen
import com.example.askit.ui.theme.AskItTheme
=======
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.example.askit.auth.view.SignInScreen
import com.example.askit.auth.view.SignUpScreen
import com.example.askit.ui.theme.AskitTheme
>>>>>>> origin/Askit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
<<<<<<< HEAD
            AskItTheme { // Use your app's theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
=======
            AskitTheme  {
                Surface(modifier = Modifier.fillMaxSize(),
                color= Color.White
                ) {
                    AskitAuthScreen()
>>>>>>> origin/Askit
                }
            }
        }
    }
}

<<<<<<< HEAD

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }
    }
}
=======
@Composable
fun AskitAuthScreen() {
    var isSignIn by remember { mutableStateOf(true) }

    if (isSignIn) {
        SignInScreen(onSwitchToSignUp = { isSignIn = false })
    } else {
        SignUpScreen(onSwitchToSignIn = { isSignIn = true })
    }
}
>>>>>>> origin/Askit
