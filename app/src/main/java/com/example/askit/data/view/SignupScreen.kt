package com.example.askit.data.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.askit.data.viewmodel.AuthViewModel
import com.example.askit.data.viewmodel.AuthViewModel
@Composable
fun SignUpScreen(
    onSwitchToSignIn: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Snackbar for errors
    snackbarMessage?.let { message ->
        Snackbar(
            action = {
                TextButton(onClick = { snackbarMessage = null }) {
                    Text("OK")
                }
            },
            modifier = Modifier.padding(16.dp)
        ) { Text(message) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Askit", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Sign Up", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
                    if (!email.matches(emailPattern)) {
                        snackbarMessage = "Enter a valid email address"
                        return@Button
                    }

                    isLoading = true
                    viewModel.signUp(name, email, password) { success, message ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        } else {
                            snackbarMessage = message ?: "Signup Failed"
                        }
                    }
                } else {
                    snackbarMessage = "Please fill all fields"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create Account")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Sign In",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onSwitchToSignIn() }
        )
    }
}