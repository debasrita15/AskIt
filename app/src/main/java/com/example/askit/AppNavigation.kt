package com.example.askit

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.askit.data.view.*
import com.example.askit.data.viewmodel.AuthViewModel
import com.example.askit.data.viewmodel.ProfileViewModel
import com.example.askit.data.viewmodel.QuestionViewModel

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun AppNavigation(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        // ✅ Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate("signin") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Sign In
        composable("signin") {
            SignInScreen(
                onSwitchToSignUp = { navController.navigate("signup") },
                onSignInSuccess = {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Sign Up
        composable("signup") {
            SignUpScreen(
                onSwitchToSignIn = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Home
        composable("home") {
            HomeScreen(navController = navController)
        }

        // ✅ Ask Question
        composable("askQuestion") {
            AskQuestionScreen(navController = navController)
        }

        // ✅ Profile
        composable("profile") {
            ProfileScreen(
                viewModel = profileViewModel,
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onSettingsClick = { navController.navigate("settings") },
                onMyQuestionsClick = { navController.navigate("myquestions") },
                onMyAnswersClick = { navController.navigate("myanswers") }
            )
        }

        // ✅ Settings
        composable("settings") {
            SettingsScreen(navController = navController)
        }

        // ✅ Change Username
        composable("changeUsername") {
            ChangeUsernameScreen(onBack = { navController.popBackStack() })
        }

        // ✅ Change Password
        composable("changePassword") {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }

        // ✅ Change Email
        composable("changeEmail") {
            ChangeEmailScreen(onBack = { navController.popBackStack() })
        }

        composable("myquestions") {
            MyQuestionsScreen(
                profileViewModel = profileViewModel,
                navController = navController
            )
        }

        composable("myanswers") {
            MyAnswersScreen(
                profileViewModel = profileViewModel,
                navController = navController
            )
        }

        // Navigate to answer page with questionId
        composable("answer/{questionId}") { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId") ?: return@composable
            AnswerPage(
                questionId = questionId,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}

