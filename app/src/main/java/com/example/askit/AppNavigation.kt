package com.example.askit

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.askit.data.view.*
import com.example.askit.data.viewmodel.AnswerViewModel
import com.example.askit.data.viewmodel.ProfileViewModel
import com.example.askit.data.viewmodel.QuestionViewModel

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun AppNavigation(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val questionViewModel: QuestionViewModel = viewModel()
    val answerViewModel: AnswerViewModel = viewModel()

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
                        popUpTo("splash") { inclusive = true }
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
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // ✅ Home Screen
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

        // ✅ My Questions
        composable("myquestions") {
            MyQuestionsScreen(
                navController = navController,
                questionViewModel = questionViewModel
            )
        }


        // ✅ My Answers
        composable("myanswers") {
            MyAnswersScreen(
                answerViewModel = answerViewModel,
                navController = navController
            )
        }

        composable("answer/{questionId}/{questionTitle}") { backStackEntry ->
            val questionId =
                backStackEntry.arguments?.getString("questionId") ?: return@composable
            val questionTitle = backStackEntry.arguments?.getString("questionTitle") ?: ""
            AnswerPage(
                questionTitle = questionTitle,
                questionId = questionId,
                navController = navController,
                answerViewModel = answerViewModel
            )
        }
        composable("notifications") {
            NotificationScreen(navController = navController)
        }
    }
}