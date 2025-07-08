package com.example.askit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.askit.auth.view.SignInScreen
import com.example.askit.auth.view.SignUpScreen
import com.example.askit.ui.theme.AskitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            AskItTheme { // Use your app's theme
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    val navController = rememberNavController()
//                    AppNavigation(navController = navController)
            AskitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AskitAuthScreen()
                }
            }
            setContent {
                AppNavigation()
            }
        }
    }
}
//    }
//}


//@Composable
//fun AppNavigation(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = "splash") {
//
//        composable("splash") {
//            SplashScreen(navController)
//        }
//    }
//}
@Composable
fun AskitAuthScreen() {
    var isSignIn by remember { mutableStateOf(true) }

    if (isSignIn) {
        SignInScreen(onSwitchToSignUp = { isSignIn = false })
    } else {
        SignUpScreen(onSwitchToSignIn = { isSignIn = true })
    }
}