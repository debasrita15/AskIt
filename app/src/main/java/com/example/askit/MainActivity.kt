package com.example.askit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
 profile
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.askit.auth.view.ProfileScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
 main
import com.example.askit.auth.view.SignInScreen
import com.example.askit.auth.view.SignUpScreen
import com.example.askit.ui.theme.AskitTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.askit.auth.viewmodels.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
 profile
            AskitTheme {
                val firebaseAuth = remember { FirebaseAuth.getInstance() }
                val db = remember { FirebaseFirestore.getInstance() }

                var isUserLoggedIn by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(true) }
                var isSignIn by remember { mutableStateOf(true) }

                // ✅ Check Firebase auth + Firestore profile on first launch
                LaunchedEffect(Unit) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        db.collection("users").document(user.uid).get()
                            .addOnSuccessListener { doc ->
                                isUserLoggedIn = doc.exists()
                                isLoading = false
                            }
                            .addOnFailureListener {
                                isUserLoggedIn = false
                                isLoading = false
                            }
                    } else {
                        isUserLoggedIn = false
                        isLoading = false
                    }
                }

                when {
                    isLoading -> {
                        Text("Loading...")
                    }

                    isUserLoggedIn -> {
                        val currentUserUid = firebaseAuth.currentUser?.uid
                        val profileViewModel: ProfileViewModel = viewModel(
                            key = currentUserUid
                        )

                        ProfileScreen(
                            onLogout = {
                                firebaseAuth.signOut()
                                isUserLoggedIn = false
                            },
                            viewModel = profileViewModel
                        )
                    }

                    else -> {
                        if (isSignIn) {
                            SignInScreen(
                                onSwitchToSignUp = { isSignIn = false },
                                onLoginSuccess = {
                                    val user = firebaseAuth.currentUser
                                    user?.let {
                                        db.collection("users").document(it.uid).get()
                                            .addOnSuccessListener { doc ->
                                                if (doc.exists()) {
                                                    isUserLoggedIn = true
                                                }
                                            }
                                    }
                                }
                            )
                        } else {
                            SignUpScreen(
                                onSwitchToSignIn = { isSignIn = true },
                                onSignUpSuccess = {
                                    isUserLoggedIn = true
                                }
                            )
                        }
                    }

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
 main
                }
            }
            setContent {
                AppNavigation()
            }
        }
    }
}
 profile

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
 main
