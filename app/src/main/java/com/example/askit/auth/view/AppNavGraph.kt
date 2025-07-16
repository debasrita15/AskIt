package com.example.askit.auth.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.askit.auth.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "signin") {

        // Sign In Screen
        composable("signin") {
            SignInScreen(
                onSwitchToSignUp = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("profile") },
                viewModel = authViewModel
            )
        }

        // Sign Up Screen
        composable("signup") {
            SignUpScreen(
                onSwitchToSignIn = { navController.navigate("signin") },
                onSignUpSuccess = { navController.navigate("profile") },
                viewModel = authViewModel
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("signin") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }
    }
}
