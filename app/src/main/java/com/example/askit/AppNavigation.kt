package com.example.askit

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.askit.auth.view.SignInScreen
import com.example.askit.auth.view.SignUpScreen
import com.example.askit.ui.screens.SplashScreen

@Composable
 fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("signin") {
            SignInScreen(
                onSwitchToSignUp = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSwitchToSignIn = { navController.navigate("home") }
            )
        }
    }
}