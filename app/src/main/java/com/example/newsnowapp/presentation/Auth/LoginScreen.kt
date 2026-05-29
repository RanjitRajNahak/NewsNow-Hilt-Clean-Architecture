package com.example.app1.ui.screens

import android.R
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app1.ui.component.NewsTextField
import com.example.app1.viewmodel.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel,onSignUpClick: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current // Used to show Toast messages

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF5F7FA), Color(0xFFB8C6DB))
                )
            )
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.4f)
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NewsNow",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1565C0)
                    )
                )
                Text(
                    text = "Insight in your pocket",
                    color = Color.DarkGray.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(60.dp))

                NewsTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                )

                Spacer(modifier = Modifier.height(16.dp))

                NewsTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true
                )

                TextButton(
                    onClick = {  },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = Color(0xFF1565C0))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        // ✅ FIX: Process the actual database login validation checks
                        authViewModel.login(email, password) { isSuccess, responseMessage ->
                            if (isSuccess) {
                                onLoginSuccess() // Session is saved automatically via VM
                            } else {
                                // Shows Toast for "User Not Registered" or "Incorrect Password"
                                Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },

                    // Disable click action unless the user typed in both inputs
                    enabled = email.isNotEmpty() && password.isNotEmpty(),

                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Login", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onSignUpClick) {
                    Row {
                        Text("Don't have account? ", color = Color.DarkGray)
                        Text("Sign Up", color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}