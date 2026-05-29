package com.example.app1.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.app1.data.local.User
import com.example.app1.ui.component.NewsTextField
import com.example.app1.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Don't flag mismatched passwords while the user is still filling out the initial password field
    val passwordsMatch by remember {
        derivedStateOf {
            confirmPassword.isEmpty() || password == confirmPassword
        }
    }

    // Room Database checking hooks for real-time validation
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            authViewModel.checkIfEmailExists(email)
        }
    }

    // Clean structural evaluation block to enable/disable button safely
    val isFormValid by remember {
        derivedStateOf {
            name.trim().isNotEmpty() &&
                    email.trim().isNotEmpty() &&
                    password.isNotEmpty() &&
                    confirmPassword.isNotEmpty() &&
                    password == confirmPassword &&
                    !authViewModel.isEmailAlreadyRegistered
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF5F7FA), Color(0xFFB8C6DB)))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .imePadding(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1565C0)
                    )
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Unified custom components used smoothly
                NewsTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                Spacer(modifier = Modifier.height(12.dp))

                NewsTextField(value = email, onValueChange = { email = it }, label = "Email")

                // Error feedback hook if Email lookup triggers positive response from Room Dao
                if (authViewModel.isEmailAlreadyRegistered && email.isNotEmpty()) {
                    Text(
                        text = "Email already registered",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 12.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                NewsTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)
                Spacer(modifier = Modifier.height(12.dp))

                NewsTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    isPassword = true
                )

                // Validation logic display triggers safely down here
                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text(
                        text = "Passwords do not match",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 12.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()

                        val newUser = User(
                            email = email.trim(),
                            name = name.trim(),
                            password = password
                        )

                        authViewModel.signUp(newUser) {
                            Toast.makeText(context, "Account Registered successfully!", Toast.LENGTH_SHORT).show()
                            onBackToLogin()
                        }
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Create Account", fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = onBackToLogin) {
                    Text("Already have account? ", color = Color.DarkGray)
                    Text("log in", color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}






